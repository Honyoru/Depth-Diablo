<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.2" name="arena" tilewidth="32" tileheight="32" tilecount="16" columns="4">
 <image source="arena.png" width="128" height="128"/>
 <tile id="0">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_corner_top_left"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_top"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_corner_top_right"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="floor_variant_1"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_left"/>
  </properties>
 </tile>
 <tile id="5">
  <properties>
   <property name="collides" type="bool" value="false"/>
   <property name="name" value="floor"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_right"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="collides" type="bool" value="false"/>
   <property name="name" value="floor_variant_2"/>
   <property name="spawn" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="8">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_corner_bottom_left"/>
  </properties>
 </tile>
 <tile id="9">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_bottom"/>
  </properties>
 </tile>
 <tile id="10">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_corner_bottom_right"/>
  </properties>
 </tile>
 <tile id="11">
  <properties>
   <property name="collides" type="bool" value="false"/>
   <property name="name" value="floor_variant_3"/>
   <property name="spawn_monster" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="12">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_inner_corner_top_left"/>
  </properties>
 </tile>
 <tile id="13">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_inner_corner_top_right"/>
  </properties>
 </tile>
 <tile id="14">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_inner_corner_bottom_left"/>
  </properties>
 </tile>
 <tile id="15">
  <properties>
   <property name="collides" type="bool" value="true"/>
   <property name="name" value="wall_inner_corner_bottom_right"/>
  </properties>
 </tile>
 <wangsets>
  <wangset name="Unnamed Set" type="mixed" tile="-1">
   <wangcolor name="" color="#ff0000" tile="-1" probability="1"/>
  </wangset>
 </wangsets>
</tileset>
