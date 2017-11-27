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

(ns transit.player.player-methods
  (:require
   [transit.config.constants :refer [FREE METERED]]
   [transit.instr.instrument :refer [select-instrument]]
   [transit.player.structures.base-structure :refer [set-struct-id]]
   [transit.player.structures.gesture :refer [create-gesture-struct]]
   [transit.player.structures.random-event :refer [create-random-struct]]
   [transit.player.structures.motif :refer [create-motif]]
   [transit.melody.melody-event :refer [get-dur-millis-from-melody-event
                                        get-note-from-melody-event]]
   [transit.melody.pitch :refer [select-random-key
                                 select-random-pitch]]
   [transit.melody.rhythm :refer [select-random-rhythm]]
   [transit.melody.volume :refer [select-random-volume]]
   )
  (:import
   [transit.player.structures.gesture Gesture]
   [transit.player.structures.random_event RandomEvent]
   [transit.player.structures.motif Motif]
   )
  )

(defrecord MethodInfo [method weight created-at])

;; method return values
(def OK 1)  ;; Method completed normally
(def CONTINUE 1)  ;; Processing complete - do not call additional methods
(def NEW-MELODY 2)  ;; Processing complete - last melody event is new
(def NEXT-METHOD 3)  ;; Select and call another method

(defn create-method-info
  [method weight]
  (MethodInfo. method weight (System/currentTimeMillis))
  )

(defn remove-methods
  " remove methods from player
    returns: a player with methods removed from :methods"
  [player & methods]
  (defn remove-meth?
    [meth-rec]
    (not (some #(= (:method meth-rec) %) methods))
    )

  (assoc player
         :methods (filter remove-meth? (:methods player)))
  )

(defn add-methods
  [mthds & methods-weights]
  (apply conj
         mthds
         (for [[m w] (partition 2 methods-weights)] (create-method-info m w))
         ))

(defn remove-structure-type
  [player structr-type & {:keys [:time] :or {time nil}}]
  (if (list? structr-type)
    (assoc player
           :structures
           (vec (for [structr (:Structures player)
                      :when (not-any? #{structr} structr-type)]
                  struct
                  )))
    (assoc player
           :structures
           (vec (filter #(not= structr-type (type %)) (:structures player)))
           ))

  )

(defn next-struct-id
  [player]
  (str (:id player) "-" (str (:next-struct-num player)))
  )

(defn add-structure
  [player structure]
  (assoc player
         :structures (assoc (:structures player)
                                   (count (:structures player))
                                   (set-struct-id structure
                                                  (next-struct-id player))
                                   )
         :next-struct-num (inc (:next-struct-num player))
         ))

;; --------------------------------------------------


(defn set-behavior
  [[ensemble player melody player-id rtn-map]]
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn play-random-rest
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-random-rest  ******" player-id)
  [ensemble
   (add-structure player
                  (create-random-struct :internal-strength 1 :rest? true))
   melody
   player-id
   {:status OK}
   ]
  )

(defn play-random
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-random  ******" player-id)
  [ensemble
   (add-structure player
                  (create-random-struct :internal-strength 1))
   melody
   player-id
   {:status OK}
   ]
  )

(defn play-rest
  [[ensemble player melody player-id rtn-map]]
  (println "******  play-rest  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(declare build-gesture)
(declare build-motif)
(defn select-instrument-for-player
  [[ensemble player melody player-id rtn-map :as args]]
  (println "******  select-instrument-for-player  ******" player-id)
  (let [cur-methods (:methods player)
        first-instrument? (nil? (:instrument player))
        new-instrument (select-instrument args)
        new-player (if first-instrument?
                     (assoc (remove-structure-type player '(Gesture
                                                            Motif
                                                            RandomEvent))
                            :instrument-info new-instrument
                            :methods (add-methods cur-methods
                                                  play-random 10
                                                  build-gesture 5
                                                  )
                            )
                     (assoc player
                            :instrument-info new-instrument
                            )
                     )

        ]
    [ensemble new-player melody player-id {:status NEXT-METHOD}]
    )
  )

(defn select-key
  [[ensemble player melody player-id rtn-map]]
  (println "******  select-key  ******" player-id)
  [ensemble
   (assoc player :key (select-random-key))
   melody
   player-id
   {:status NEXT-METHOD}]
  )

(defn select-mm
  [[ensemble player melody player-id rtn-map]]
  (println "******  select-mm  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn select-scale
  [[ensemble player melody player-id rtn-map]]
  (println "******  select-scale  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn monitor-silence
  "Watch for x amount of time for someone to play
   Notify player when someone plays
   Tell player how long you will watch for
  "
  [[ensemble player melody player-id rtn-map]]
  (println "******  monitor-silence  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn monitor-soft
  [[ensemble player melody player-id rtn-map]]
  (println "******  monitor-soft  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn sync-with-another-player
  [[ensemble player melody player-id rtn-map]]
  (println "******  sync-with-another-player  ******" player-id)
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
  (println "******  listen  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )


;; TOP DOWN METHODS

(defn build-gesture
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-gesture  ******" player-id)
  [ensemble
   (add-structure player (create-gesture-struct :internal-strength 1
                                                :type FREE)
                        )
   melody
   player-id
   {:status OK}
   ]
  )

(defn build-motif
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-motif  ******" player-id)
  [ensemble
   (add-structure player
                  (create-motif :internal-strength 1
                                :type FREE))
   melody
   player-id
   {:status OK}
   ]
  )

(defn build-melody
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-melody  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn build-countermelody
  [[ensemble player melody player-id rtn-map]]
  (println "******  build-countermelody  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn sustained-accompaniment
  [[ensemble player melody player-id rtn-map]]
  (println "******  sustained-accompaniment  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn arpegiation
  [[ensemble player melody player-id rtn-map]]
  (println "******  arpegiation  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )

(defn loop-notes
  [[ensemble player melody player-id rtn-map]]
  (println "******  loop  ******" player-id)
  [ensemble player melody player-id {:status CONTINUE}]
  )
