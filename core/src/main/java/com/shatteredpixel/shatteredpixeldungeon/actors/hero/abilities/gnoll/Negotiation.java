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
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gnoll;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class Negotiation extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		int gold = 12 + 4 * Dungeon.depth;
		gold += 8 * hero.pointsInTalent(Talent.COIN_TRICK);
		gold = Math.round(gold * (1f + 0.25f * hero.pointsInTalent(Talent.HAGGLE_BURST)));
		Dungeon.gold += gold;
		hero.sprite.showStatusWithIcon(CharSprite.NEUTRAL, Integer.toString(gold), com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText.GOLD);
		Buff.affect(hero, Discount.class, 40f);
		hero.sprite.operate(hero.pos);
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	public static class Discount extends FlavourBuff {
	}

	@Override
	public int icon() {
		return HeroIcon.FEINT;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HAGGLE_BURST, Talent.COIN_TRICK, Talent.FAIR_PRICE, Talent.HEROIC_ENERGY};
	}
}
