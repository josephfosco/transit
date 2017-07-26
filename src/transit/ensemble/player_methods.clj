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

;; method return values
(def CONTINUE 1)  ;; Processing complete - do not call additional methods
(def NEW-MELODY 2)  ;; Processing complete - last melody event is new
(def NEXT-METHOD 3)  ;; Select and call another method

(defn choose-instrument
  [[ensemble player melody player-id]]

  )


;; --------------------------------------------------


(defn set-behavior
  [[ensemble player melody player-id rtn-map]]
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn play-random-note
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-random-note  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn play-next-note
  "Use available information to select and play the next
   relevent note for this player"
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-next-note  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn play-rest
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-rest  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn select-instrument
  [[ensemble player melody player-id rtn-map]]
  (println "******  select-instrument  ******")
  (let [cur-methods (:methods player)
        add-play-random-note-method? (nil? (:instrument player))
        new-instrument nil
        new-player (if add-play-random-note-method?
                     (assoc player
                            :instrument new-instrument
                            :methods (conj cur-methods [play-random-note 10])
                            )
                     (assoc player
                            :instrument new-instrument
                            )
                     )

        ]
    [ensemble new-player melody player-id {:status NEXT-METHOD}]
    )
  )

(defn monitor-silence
  "Watch for x amount of time for someone to play
   Notify player when someone plays
   Tell player how long you will watch for
  "
  [[ensemble player melody player-id rtn-map]]
  (println "******  monitor-silence  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn monitor-soft
  [[ensemble player melody player-id rtn-map]]
  (println "******  monitor-soft  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn sync-with-another-player
  [[ensemble player melody player-id rtn-map]]
  (println "******  sync-with-another-player  ******")
  [ensemble player melody player-id {:status CONTINUE}]
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
  [[ensemble player melody player-id rtn-map]]
  (println "******  listen  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )


;; TOP DOWN METHODS

(defn build-melody
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-melody  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn build-countermelody
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-countermelody  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn sustained-accompaniment
  [[ensemble player melody player-id rtn-map]]
  (println "******  sustained-accompaniment  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn arpegiation
  [[ensemble player melody player-id rtn-map]]
  (println "******  arpegiation  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn loop-notes
  [[ensemble player melody player-id rtn-map]]
  (println "******  loop  ******")
  [ensemble player melody player-id {:status CONTINUE}]
  )
