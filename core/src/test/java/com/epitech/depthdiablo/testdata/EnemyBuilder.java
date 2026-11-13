package com.epitech.depthdiablo.testdata;

import com.epitech.depthdiablo.entity.Aabb;
import com.epitech.depthdiablo.entity.BaseStats;
import com.epitech.depthdiablo.entity.MeleeEnemy;
import com.epitech.depthdiablo.entity.Rat;
import com.epitech.depthdiablo.entity.Stat;
import com.epitech.depthdiablo.entity.StatSource;
import com.epitech.depthdiablo.entity.Zombie;

import java.nio.file.WatchEvent.Kind;
import java.util.EnumMap;
import java.util.Map;

/** Test data builder (Q116) for real enemies: a rat or a zombie, changed one value at a time. */

public final class EnemyBuilder {
  private final Kind kind;
  private final Map<Stat, Float> stats = new EnumMap<Stat.class>;
  private final Aabb bodyBox = new Aabb(0, 0, 16, 16);
  private float reach =4;
  private boolean summoned;

  private EnemyBuilder(Kind kind, float maxHp, float attack) {
    this.kind = kind;

  }

}
