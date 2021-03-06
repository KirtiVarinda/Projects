package com.example.kirti_pc.droidar.v2.simpleUi;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import v2.simpleUi.*;
import v2.simpleUi.M_HalfHalf;

/**
 * This is a first example implementation with a fixed number of 2 items in each
 * row
 * 
 * @author Simon Heinen
 * 
 */
public class M_Dashboard extends v2.simpleUi.M_Container {
	@Override
	protected void createViewsForAllModifiers(Context target,
			LinearLayout containerForAllItems) {
		boolean sizeIsMod2 = this.size() % 2 == 0;
		int size = sizeIsMod2 ? this.size() : this.size() - 1;
		for (int i = 0; i < size; i += 2) {
			containerForAllItems.addView(get2ItemViewFor(get(i), get(i + 1),
					target));
		}
		if (!sizeIsMod2) {
			// if its not a mod 2 == 0 number then add the last one manually
			containerForAllItems.addView(this.get(this.size() - 1).getView(
					target));
		}
	}

	private View get2ItemViewFor(ModifierInterface left,
			ModifierInterface right, Context context) {
		v2.simpleUi.M_HalfHalf line = new M_HalfHalf(left, right, 70, true);
		return line.getView(context);
	}
}
