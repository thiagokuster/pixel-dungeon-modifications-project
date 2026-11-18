/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Pixel Dungeon Alterado
 * Copyright (C) 2026 thiagokuster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gnoll.Negotiation;

public class ClassPerks {

	private ClassPerks() {}

	public static float goldMultiplier(Hero hero) {
		if (hero == null || hero.heroClass != HeroClass.GNOLL) return 1f;
		float multiplier = 1.25f;
		if (hero.subClass == HeroSubClass.MERCHANT) multiplier += 0.15f;
		multiplier += 0.08f * hero.pointsInTalent(Talent.FAT_PURSE);
		multiplier += 0.08f * hero.pointsInTalent(Talent.COIN_POUCH);
		multiplier += 0.05f * hero.pointsInTalent(Talent.EXTRA_COIN);
		return multiplier;
	}

	public static float shopFactor(Hero hero) {
		if (hero == null || hero.heroClass != HeroClass.GNOLL) return 1f;
		float factor = 0.90f;
		if (hero.subClass == HeroSubClass.MERCHANT) factor -= 0.08f;
		factor -= 0.04f * hero.pointsInTalent(Talent.KEEN_BARGAIN);
		factor -= 0.03f * hero.pointsInTalent(Talent.RICH_OFFERS);
		factor -= 0.04f * hero.pointsInTalent(Talent.MASTER_DEAL);
		factor -= 0.04f * hero.pointsInTalent(Talent.HAGGLE);
		if (hero.buff(Negotiation.Discount.class) != null) {
			factor -= 0.12f + 0.04f * hero.pointsInTalent(Talent.FAIR_PRICE);
		}
		return Math.max(0.55f, factor);
	}

	public static int extraVision(Hero hero) {
		if (hero == null) return 0;
		int vision = 0;
		if (hero.subClass == HeroSubClass.TRACKER) vision += 1;
		vision += hero.pointsInTalent(Talent.SHARP_EYE) / 2;
		if (hero.pointsInTalent(Talent.TRAIL_SENSE) > 0) vision += 1;
		if (hero.pointsInTalent(Talent.FAR_SCOUT) > 0) vision += 1;
		if (hero.pointsInTalent(Talent.LONG_WATCH) > 0) vision += 1;
		if (hero.heroClass == HeroClass.NECROMANCER && hero.pointsInTalent(Talent.GRAVE_SIGHT) > 0) vision += 1;
		return Math.min(3, vision);
	}

	public static boolean seesTraps(Hero hero) {
		return hero != null && (hero.subClass == HeroSubClass.TRACKER
				|| hero.pointsInTalent(Talent.TRAP_NOSE) > 0
				|| hero.pointsInTalent(Talent.KEEN_NOSE) > 0
				|| (hero.pointsInTalent(Talent.TRAIL_TRAP) > 0 && hero.buff(MindVision.class) != null));
	}

	public static int thrownBonus(Hero hero) {
		if (hero == null || hero.heroClass != HeroClass.GNOLL) return 0;
		return hero.pointsInTalent(Talent.PACK_THROW);
	}

	public static int defenseBonus(Hero hero) {
		if (hero == null) return 0;
		return hero.pointsInTalent(Talent.QUIET_STEP) + hero.pointsInTalent(Talent.SOFT_STEPS);
	}

	public static int shopUpgrades(Hero hero) {
		if (hero == null || hero.subClass != HeroSubClass.MERCHANT) return 0;
		return 1 + hero.pointsInTalent(Talent.PROMOTE_WARES);
	}
}
