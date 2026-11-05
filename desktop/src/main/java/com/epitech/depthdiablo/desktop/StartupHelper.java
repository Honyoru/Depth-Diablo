/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Note, the above license and copyright applies to this file only.
// Java port of the gdx-liftoff StartupHelper; logging goes through SLF4J (Q96).
package com.epitech.depthdiablo.desktop;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.lwjgl.system.JNI;
import org.lwjgl.system.linux.UNISTD;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game startup helper for LWJGL3.
 *
 * <ul>
 *   <li>Windows: extracts the native libraries to a folder without non-ASCII characters.
 *   <li>macOS: restarts the JVM with {@code -XstartOnFirstThread}, which LWJGL3 needs there.
 *   <li>Linux with NVIDIA drivers: restarts the JVM with {@code __GL_THREADED_OPTIMIZATIONS=0}.
 * </ul>
 *
 * <p>Based on a java-gaming.org post by kappa; original author damios.
 */
public final class StartupHelper {

  private static final Logger LOG = LoggerFactory.getLogger(StartupHelper.class);
  private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

  private StartupHelper() {}

  /**
   * Restarts the JVM when the platform needs it.
   *
   * @return true when a child JVM was started, so the caller must return right away
   */
  public static boolean startNewJvmIfRequired() {
    String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    if (osName.contains("mac")) {
      return startNewJvm(true);
    }
    if (osName.contains("windows")) {
      String programData = System.getenv("ProgramData");
      if (programData == null) {
        programData = "C:\\Temp";
      }
      String previousTmpDir = System.getProperty("java.io.tmpdir", programData);
      String previousUser = System.getProperty("user.name", "libGDX_User");
      System.setProperty("java.io.tmpdir", programData + "\\libGDX-temp");
      System.setProperty(
          "user.name",
          ("User_" + previousUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_'));
      Lwjgl3NativesLoader.load();
      System.setProperty("java.io.tmpdir", previousTmpDir);
      System.setProperty("user.name", previousUser);
      return false;
    }
    return startNewJvm(false);
  }

  private static boolean isLinuxNvidia() {
    String[] drivers =
        new File("/proc/driver")
            .list((dir, name) -> name.toUpperCase(Locale.ROOT).contains("NVIDIA"));
    return drivers != null && drivers.length > 0;
  }

  private static boolean startNewJvm(boolean isMac) {
    long processId = isMac ? LibC.getpid() : UNISTD.getpid();
    if (!isMac) {
      if (!isLinuxNvidia() || "0".equals(System.getenv("__GL_THREADED_OPTIMIZATIONS"))) {
        return false;
      }
    } else {
      if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
        return false;
      }
      long objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
      long nsThread = ObjCRuntime.objc_getClass("NSThread");
      long currentThread =
          JNI.invokePPP(nsThread, ObjCRuntime.sel_getUid("currentThread"), objcMsgSend);
      if (JNI.invokePPZ(currentThread, ObjCRuntime.sel_getUid("isMainThread"), objcMsgSend)) {
        return false;
      }
      if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + processId))) {
        return false;
      }
    }

    if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
      LOG.error("Already in a restarted child JVM; not restarting again.");
      return false;
    }

    String javaExecPath = System.getProperty("java.home") + "/bin/java";
    if (!new File(javaExecPath).exists()) {
      LOG.error(
          "No Java installation found. With a bundled JRE, set {} manually.",
          isMac ? "-XstartOnFirstThread" : "__GL_THREADED_OPTIMIZATIONS=0");
      return false;
    }

    String mainClass = System.getenv("JAVA_MAIN_CLASS_" + processId);
    if (mainClass == null) {
      StackTraceElement[] trace = Thread.currentThread().getStackTrace();
      if (trace.length == 0) {
        LOG.error("The main class could not be determined through the stack trace.");
        return false;
      }
      mainClass = trace[trace.length - 1].getClassName();
    }

    List<String> command = new ArrayList<>();
    command.add(javaExecPath);
    if (isMac) {
      command.add("-XstartOnFirstThread");
    }
    command.add("-D" + JVM_RESTARTED_ARG + "=true");
    command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(mainClass);

    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      if (!isMac) {
        builder.environment().put("__GL_THREADED_OPTIMIZATIONS", "0");
      }
      builder.inheritIO().start().waitFor();
    } catch (IOException e) {
      LOG.error("Could not restart the JVM.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOG.error("Interrupted while waiting for the restarted JVM.", e);
    }
    return true;
  }
}
