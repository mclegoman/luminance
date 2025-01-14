/*
    Perspective
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Perspective
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.util;

public class Couple<a, b> {
	private a first;
	private b second;
	public Couple(a first, b second) {
		this.first = first;
		this.second = second;
	}

	public a getFirst() {
		return first;
	}
	public b getSecond() {
		return second;
	}
	public void setFirst(a value) {
		first = value;
	}
	public void setSecond(b value) {
		second = value;
	}
}
