;    Copyright (C) 2017  Joseph Fosco. All Rights Reserved
;
;    This program is free software: you can redistribute it and/or modify
;    it under the terms of the GNU General Public License as published by
;    the Free Software Foundation, either version 3 of the License, or
;    (at your option) any later version.
;
;    This program is distributed in the hope that it will be useful,
;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;    GNU General Public License for more details.
;
;    You should have received a copy of the GNU General Public License
;    along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns transit.ensemble.player-methods)

(defn set-behavior
  [player]
  )

(defn play-random-note
  [player]
  (println "******  play-random-note  ******")
  )

(defn play-next-note
  "Use available information to select and play the next
   relevent note for this player"
  [player]
  (println "******  play-next-note  ******")
  )

(defn play-rest
  [player]
  (println "******  play-rest  ******")
  )

(defn select-instrument
  [player]
  (println "******  select-instrument  ******")
  )

(defn monitor-silence
  "Watch for x amount of time for someone to play
   Notify player when someone plays
   Tell player how long you will watch for
  "
  [player]
  (println "******  monitor-silence  ******")
  )

(defn monitor-soft
  [player]
  (println "******  monitor-soft  ******")
  )

(defn listen
  "Returns
      timestamp
      number of players playing
      current density
      high mm
      low mm
      mm trend
      most common scale
      number of players w/ most popular scale
      most popular key
      number of players with most popular key
      average length of notes
      average volume
      volume trend
  "
  [player]
  (println "******  listen  ******")

  )
