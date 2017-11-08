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

(ns transit.player.player
  (:require
   [transit.player.player-methods :refer [listen
                                          monitor-silence
                                          play-random-rest
                                          select-key
                                          select-mm
                                          select-instrument-for-player
                                          select-scale
                                          ]]
   [transit.player.structures.base-structure :refer [get-melody-fn
                                                     get-strength-fn]]
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.melody.rhythm :refer [select-random-rhythm]]
   )
  (:import transit.player.player_methods.MethodInfo)
  )

(defrecord Player [id
                   key
                   scale
                   mm
                   instrument-info
                   methods
                   sampled-melodies
                   structures
                   next-struct-num])

(defn get-initial-player-methods
  []
  (let [time (System/currentTimeMillis)]
      [
       (MethodInfo. listen 2 time)
       (MethodInfo. play-random-rest 2 time)
       (MethodInfo. monitor-silence 1 time)
       (MethodInfo. select-key 1 time)
       (MethodInfo. select-mm 1 time)
       (MethodInfo. select-instrument-for-player 3 time)
       (MethodInfo. select-scale 1 time)
       ]
    )
  )

(defn create-player
  [& {:keys [:id]}]
  (Player. id
           nil  ;; key
           nil  ;; scale
           nil  ;; mm
           nil  ;; instrument
           (get-initial-player-methods)
           nil  ;; sampled-melodies
           []   ;; structures
           0    ;; next-struct-num
           )
  )

(defn get-player-id
  [player]
  (:id player))

(defn get-player-instrument-info
  [player]
  (:instrument-info player))

(defn find-high-strength
  " A reducing function to find the index of the struct with the highest strength
    Calls strength-fn from struct and if it is > then the current highest
      strength (returns a result containing information for the current struct
    rslt is an array of 3 elements
      [ index of structure with the highest strength so far
        value of highest strength so far
        index for the next structure
      ]
  "
  [rslt struct]
  (let [new-strength ((get-strength-fn struct) struct)]
    (if (< (second rslt) new-strength)
      [(rslt 2) new-strength (inc (rslt 2))]
      [(first rslt) (second rslt) (inc (rslt 2))]
      )
    )
  )

(defn create-random-rest-melody-event
  [player-id event-id]
  (create-melody-event :melody-event-id event-id
                       :note nil
                       :dur-info (select-random-rhythm)
                       :volume nil
                       :instrument-info nil
                       :player-id player-id
                       :event-time nil
                       )
  )

(defn get-next-melody-event
  [ensemble player melody player-id]
  (let [plyr-structs (:structures player)
        max-strength-index (rand-nth (reduce find-high-strength
                                             [0 0 0]
                                             plyr-structs
                                             ))
        melody-struct (get plyr-structs max-strength-index)
        ]
    (if melody-struct
      (do
        (let [[new-struct melody-event]
              ((get-melody-fn melody-struct)
               ensemble
               player
               melody
               player-id
               melody-struct
               (inc (:melody-event-id (last melody)))
               )]
          (if melody-event
            [(assoc player
                    :structures
                    (assoc plyr-structs max-strength-index new-struct))
             melody-event
             ]
            [player
             (create-random-rest-melody-event
              player-id
              (inc (:melody-event-id (last melody))))
             ]
            )))
      [player
       (create-random-rest-melody-event player-id
                                        (inc (:melody-event-id (last melody))))
       ]
      )
    )
  )

(defn print-player
  "Pretty Print a player map

  player - the player map to print"
  [player & {:keys [prnt-full-inst-info]
             :or {prnt-full-inst-info false}}]
  (let [sorted-keys (sort (keys player))]
    (println "player: " (get-player-id player) "current time: " (System/currentTimeMillis))
    (doseq [player-key sorted-keys]
      (cond
        (= player-key :methods)
        (do
          (println " " player-key)
          (doseq [method-info (get player :methods)]
            (println "   " (type (:method method-info))
                     " weight: " (:weight method-info)
                     " time: " (time method-info)
                     )
            ))
        (and (= player-key :instrument-info) (= prnt-full-inst-info false))
        (do
          (println (format "%-29s" (str "  " player-key " :name")) "-" (:name (:instrument (:instrument-info player))))
          (println (format "%-29s" (str "  " player-key " :range-lo")) "-" (:range-lo (:instrument-info player)))
          (println (format "%-29s" (str "  " player-key " :range-hi")) "-" (:range-hi (:instrument-info player))))
        :else
        (println (format "%-20s" (str "  " player-key)) "-" (get player player-key)))
      )
    (println "end player: " (get-player-id player) "current time: " (System/currentTimeMillis))
    (prn)
    )
  )
