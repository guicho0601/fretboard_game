/*
 * Copyright 2016 andryr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lrgt.fretboardgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lramos on 24/04/16.
 */
public class Notes {

    List<Pitch> pitches;
    List<String> notes;

    Notes() {
        this.pitches = new ArrayList<>();
        this.notes = new ArrayList<>();
        loadPitches();
        loadNotes();
    }

    public Pitch closestPitch(float freq) {
        Pitch closest = null;
        float dist = Float.MAX_VALUE;
        for (Pitch pitch : pitches) {
            float d = Math.abs(freq - pitch.frequency);
            if (d < dist) {
                closest = pitch;
                dist = d;
            }
        }
        return closest;
    }

    int closestPitchIndex(float freq) {
        int index = -1;
        float dist = Float.MAX_VALUE;
        for (int i = 0; i < pitches.size(); i++) {
            Pitch pitch = pitches.get(i);
            float d = Math.abs(freq - pitch.frequency);
            if (d < dist) {
                index = i;
                dist = d;
            }
        }
        return index;
    }

    private void loadPitches() {
        pitches.add(new Pitch(73.42F, "D"));
        pitches.add(new Pitch(77.78F, "D#/Eb"));
        pitches.add(new Pitch(82.41F, "E"));
        pitches.add(new Pitch(87.31F, "F"));
        pitches.add(new Pitch(92.50F, "F#/Gb"));
        pitches.add(new Pitch(98.00F, "G"));
        pitches.add(new Pitch(103.83F, "G#/Ab"));
        pitches.add(new Pitch(110.00F, "A"));
        pitches.add(new Pitch(116.54F, "A#/Bb"));
        pitches.add(new Pitch(123.47F, "B"));
        pitches.add(new Pitch(130.81F, "C"));
        pitches.add(new Pitch(138.59F, "C#/Db"));
        pitches.add(new Pitch(146.83F, "D"));
        pitches.add(new Pitch(155.56F, "D#/Eb"));
        pitches.add(new Pitch(164.81F, "E"));
        pitches.add(new Pitch(174.61F, "F"));
        pitches.add(new Pitch(185.00F, "F#/Gb"));
        pitches.add(new Pitch(196.00F, "G"));
        pitches.add(new Pitch(207.65F, "G#/Ab"));
        pitches.add(new Pitch(220.00F, "A"));
        pitches.add(new Pitch(233.08F, "A#/Bb"));
        pitches.add(new Pitch(246.94F, "B"));
        pitches.add(new Pitch(261.63F, "C"));
        pitches.add(new Pitch(277.18F, "C#/Db"));
        pitches.add(new Pitch(293.66F, "D"));
        pitches.add(new Pitch(311.13F, "D#/Eb"));
        pitches.add(new Pitch(329.63F, "E"));
        pitches.add(new Pitch(349.23F, "F"));
        pitches.add(new Pitch(369.99F, "F#/Gb"));
        pitches.add(new Pitch(392.00F, "G"));
        pitches.add(new Pitch(415.30F, "G#/Ab"));
        pitches.add(new Pitch(440.00F, "A"));
        pitches.add(new Pitch(466.16F, "A#/Bb"));
        pitches.add(new Pitch(493.88F, "B"));
        pitches.add(new Pitch(523.25F, "C"));
        pitches.add(new Pitch(554.37F, "C#/Db"));
        pitches.add(new Pitch(587.33F, "D"));
        pitches.add(new Pitch(622.25F, "D#/Eb"));
        pitches.add(new Pitch(659.26F, "E"));
    }

    private void loadNotes() {
        notes.add("C");
        notes.add("C#/Db");
        notes.add("D");
        notes.add("D#/Eb");
        notes.add("E");
        notes.add("F");
        notes.add("F#/Gb");
        notes.add("G");
        notes.add("G#/Ab");
        notes.add("A");
        notes.add("A#/Bb");
        notes.add("B");
    }

}
