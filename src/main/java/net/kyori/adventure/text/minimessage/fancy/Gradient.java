/*
 * This file is part of adventure-text-minimessage, licensed under the MIT License.
 *
 * Copyright (c) 2018-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text.minimessage.fancy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Gradient implements Fancy {

  private int index = 0;
  private int colorIndex = 0;

  private float factorStep = 0;
  private TextColor[] colors;
  private float phase;
  private boolean negativePhase = false;

  public Gradient() {
    this(0, TextColor.fromHexString("#ffffff"), TextColor.fromHexString("#000000"));
  }

  public Gradient(float phase, TextColor... colors) {
    this.colors = colors;
    this.phase = phase;
  }

  @Override
  public void init(int size) {
    final int sectorLength = size / (this.colors.length - 1);
    this.factorStep = 1.0f / (sectorLength + this.index - 1);
    if (this.phase < 0) {
      this.negativePhase = true;
      final List<TextColor> l = Arrays.asList(this.colors);
      Collections.reverse(l);
      this.colors = l.toArray(new TextColor[0]);
      this.phase = 1 + this.phase;
    }
    this.phase = this.phase * (sectorLength - 1);
    this.index = 0;
  }

  @Override
  public Component apply(Component current) {
    return current.color(getColor());
  }

  private TextColor getColor() {
    // color switch needed?
    if (factorStep * index > 1) {
      colorIndex++;
      index = 0;
    }

    float factor = factorStep * (index++ + phase);
    // loop around if needed
    if (factor > 1) {
      factor = 1 - (factor - 1);
    }
    if (negativePhase && colors.length % 2 != 0) {
      // flip the gradient segment for to allow for looping phase -1 through 1
      return interpolate(colors[colorIndex + 1], colors[colorIndex], factor);
    } else {
      return interpolate(colors[colorIndex], colors[colorIndex + 1], factor);
    }
  }

  private TextColor interpolate(TextColor color1, TextColor color2, float factor) {
    return TextColor.of(
      Math.round(color1.red() + factor * (color2.red() - color1.red())),
      Math.round(color1.green() + factor * (color2.green() - color1.green())),
      Math.round(color1.blue() + factor * (color2.blue() - color1.blue()))
    );
  }
}
