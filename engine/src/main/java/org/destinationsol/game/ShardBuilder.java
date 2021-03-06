/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.ship.ShipBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShardBuilder {
    public static final float MIN_SCALE = .07f;
    public static final float MAX_SCALE = .12f;
    public static final float SIZE_TO_SHARD_COUNT = 13f;
    private static final float MAX_ROT_SPD = 5f;
    private static final float MAX_SPD = 4f;
    private final CollisionMeshLoader myCollisionMeshLoader;
    private final List<TextureAtlas.AtlasRegion> myTexs;

    public ShardBuilder() {
        myCollisionMeshLoader = new CollisionMeshLoader("engine:miscCollisionMeshes");
        myTexs = Assets.listTexturesMatching("engine:shard_.*");
    }

    public void buildExplosionShards(SolGame game, Vector2 pos, Vector2 baseSpd, float size) {
        int count = (int) (size * SIZE_TO_SHARD_COUNT);
        for (int i = 0; i < count; i++) {
            Shard s = build(game, pos, baseSpd, size);
            game.getObjMan().addObjDelayed(s);
        }
    }

    public Shard build(SolGame game, Vector2 basePos, Vector2 baseSpd, float size) {

        ArrayList<Drawable> drawables = new ArrayList<>();
        float scale = SolRandom.randomFloat(MIN_SCALE, MAX_SCALE);
        TextureAtlas.AtlasRegion tex = SolRandom.randomElement(myTexs);
        float spdAngle = SolRandom.randomFloat(180);
        Vector2 pos = new Vector2();
        SolMath.fromAl(pos, spdAngle, SolRandom.randomFloat(size));
        pos.add(basePos);
        Body body = myCollisionMeshLoader.getBodyAndSprite(game, tex, scale, BodyDef.BodyType.DynamicBody, pos, SolRandom.randomFloat(180), drawables, ShipBuilder.SHIP_DENSITY, DrawableLevel.PROJECTILES);

        body.setAngularVelocity(SolRandom.randomFloat(MAX_ROT_SPD));
        Vector2 spd = SolMath.fromAl(spdAngle, SolRandom.randomFloat(MAX_SPD));
        spd.add(baseSpd);
        body.setLinearVelocity(spd);
        SolMath.free(spd);

        Shard shard = new Shard(body, drawables);
        body.setUserData(shard);
        return shard;
    }
}
