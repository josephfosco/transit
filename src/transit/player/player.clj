;    Copyright (C) 2017-2018  Joseph Fosco. All Rights Reserved
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
                                          select-key
                                          select-mm
                                          select-instrument-for-player
                                          select-scale
                                          ]]
   [transit.player.player-utils :refer [create-random-rest-melody-event]]
   [transit.player.structures.base-structure :refer [get-cleanup-fn
                                                     get-melody-fn
                                                     get-strength-fn
                                                     get-structr-id]]
   [transit.melody.melody-event :refer [get-structr-id-from-melody-event]]
   [transit.util.random :refer [weighted-choice]]
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
                   next-struct-num
                   can-schedule? ;; is not waiting for msg(s)
                   ])

(defn get-initial-player-methods
  []
  (let [time (System/currentTimeMillis)]
      [
       (MethodInfo. listen 2 time)
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
           true ;; can-schedule?
           )
  )

(defn get-player-id
  [player]
  (:id player))

(defn get-player-instrument-info
  [player]
  (:instrument-info player))

(defn find-high-strength
  " A reducing function to find the index of the structr with the highest strength
    Calls strength-fn from structr and if it is > then the current highest
      strength (returns a result containing information for the current structr
    rslt is an array of 3 elements
      [ index of structure with the highest strength so far
        value of highest strength so far
        index for the next structure
      ]
  "
  [rslt structr]
  (let [new-strength ((get-strength-fn structr) structr)]
    (if (< (second rslt) new-strength)
      [(rslt 2) new-strength (inc (rslt 2))]
      [(first rslt) (second rslt) (inc (rslt 2))]
      )
    )
  )

(defn cleanup-structr-id
  "Find structure for structr-id in player
   if found, call structr's cleanup-fn
             update player with new struct which was returned from cleanup-fn
             return updated player
   if not found, return player as it is
  "
  [player structr-id]
  (let [[structr-ndx  prev-structr] (first
                                     (keep-indexed
                                      #(if (= (get-structr-id %2) structr-id)
                                         [%1 %2]
                                         nil
                                         )
                                      (:structures player)
                                      ))
        cleanup-fn (if prev-structr (get-cleanup-fn prev-structr) nil)
        ]
    (if cleanup-fn
        (assoc player
               :structures
               (assoc (:structures player)
                      structr-ndx
                      (cleanup-fn prev-structr)))
      player
      ))
  )

(defn get-struct-strength
  [structr]
  ((get-strength-fn structr) structr))

(defn get-next-melody-event
  " Returns a melody-event and an updated player"
  [ensemble player melody player-id]
  (let [plyr-structs (:structures player)
        all-weights (mapv get-struct-strength plyr-structs)
        selected-struct-index (if (> (count all-weights) 0)
                                (weighted-choice all-weights)
                                nil)
        melody-struct (if selected-struct-index
                        (get plyr-structs selected-struct-index)
                        nil)
        [new-struct melody-event] (if melody-struct
                                    ((get-melody-fn melody-struct)
                                     ensemble
                                     player
                                     melody
                                     player-id
                                     melody-struct
                                     (inc (:melody-event-id (last melody)))
                                     )
                                    [nil nil]
                                    )
        prev-structr-id (get-structr-id-from-melody-event
                         (get melody (dec (count melody))))
        ;; if this melody-event has a different structr-id than
        ;; the prev melody-event, call cleanup-structr-id to update the player
        ;; by calling the previous structr-id cleanup-fn
        cleanup-player (if (or (not prev-structr-id)
                               (and melody-event
                                    (= (get-structr-id-from-melody-event
                                        melody-event)
                                       prev-structr-id))
                               )
                          player
                          (cleanup-structr-id player prev-structr-id))
        ]
    (if melody-event
      [(assoc cleanup-player
              :structures
              (assoc (:structures cleanup-player)
                     selected-struct-index
                     new-struct))
       melody-event
       ]
      [cleanup-player
       (create-random-rest-melody-event
        player-id
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
                     " created-at:: " (:created-at method-info)
                     )
            ))
        (and (= player-key :instrument-info) (= prnt-full-inst-info false))
        (do
          (println (format "%-29s" (str "  " player-key " :name")) "-" (:name (:instrument (:instrument-info player))))
          (println (format "%-29s" (str "  " player-key " :range-lo")) "-" (:range-lo (:instrument-info player)))
          (println (format "%-29s" (str "  " player-key " :range-hi")) "-" (:range-hi (:instrument-info player))))
        (= player-key :structures)
        (do
          (println " " player-key)
          (doseq [structr (:structures player)] (println "   " structr))
          )
        :else
        (println (format "%-20s" (str "  " player-key)) "-" (get player player-key)))
      )
    (println "end player: " (get-player-id player) "current time: " (System/currentTimeMillis))
    (prn)
    )
  )
