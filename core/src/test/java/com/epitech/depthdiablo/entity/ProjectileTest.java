package com.epitech.depthdiablo.entity;

import static com.epitech.depthdiablo.testdata.CharacterBuilder.aCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.epitech.depthdiablo.GameConstants;
import com.epitech.depthdiablo.combat.Faction;
import com.epitech.depthdiablo.engine.DamageDealt;
import com.epitech.depthdiablo.engine.EntityDied;
import com.epitech.depthdiablo.engine.GameEvent;
import com.epitech.depthdiablo.engine.World;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;


/** Projectiles (GDD 1.3.1, 1.5): they fly straight and hurt the first enemy they touch. */
class ProjectileTest {
  private static final float TEN_PIXELS_PER_TICK = 10F*GameConstants.TICKS_PER_SECOND;
  private static final int LIFETIME_TICKS = 30;
  private final World world = new World();

  /** Shoots every arrow; on the ENEMY side, like the Skeleton Archer. */
  private final Character owner = aCharacter()withWorld(world).worldBuild();
  private Arrow arrowAt(float x, float y, float aimDegrees) {
    Arrow arrow = new Arrow( new Aabb (x,y,16,16), owner,aimDegrees,TEN_PIXELS_PER_TICK,LIFETIME_TICKS,world);
    world.add(arrow);
    return arrow;
  }

  private final Character characterAt(float x, float y,Faction side) {
    Aabb box = new Aabb(x, y, 16, 16)
  }
}
