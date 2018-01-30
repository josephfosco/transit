;    Copyright (C) 2018  Joseph Fosco. All Rights Reserved
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

(ns transit.player.structures.listen
  (:require
   [transit.melody.dur-info :refer [create-dur-info]]
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.util.random :refer [random-int]]
   [transit.player.structures.base-structure :refer [create-base-structure
                                                     get-internal-strength
                                                     get-structr-id
                                                     set-internal-strength]]
   )
)

(def ^:private min-listen-millis 2500)
(def ^:private max-listen-millis 10000)

(defrecord Listen [base])

(defn sub-ensemble-status
  [structr player]
  (println "$$$$$$$$$$$ sub-ensemble-status $$$$$$$$$$$$$")
  )

(defn get-rest-event
  [player next-id listen-structr]
  (create-melody-event
   :melody-event-id next-id
   :note nil
   :dur-info (create-dur-info
              :dur-millis (random-int min-listen-millis max-listen-millis))
   :volume nil
   :instrument-info nil
   :player-id (:id player)
   :event-time nil
   :structr-id (get-structr-id listen-structr)
   )
  )

(defn get-listen-melody-event
  [ensemble player melody player-id listen-structr next-id]
  (println "#### getting listen melody event ####")
  (let [new-int-strength (- (get-internal-strength listen-structr)
                            (random-int
                             1 (int (/ (get-internal-strength listen-structr)
                                       2))))
        ]
    [
     (if (not= new-int-strength 0)
       (set-internal-strength listen-structr
                              (- (get-internal-strength listen-structr)
                                 (random-int
                                  1 (int (/ (get-internal-strength listen-structr)
                                            2))))
                              )
       nil
       )
     (get-rest-event player next-id listen-structr)
     ]
    ))

(defn create-listen-structr
  [& {:keys [structr-id
             internal-strength
             external-strength
             ] :or
      {internal-strength 0
       external-strength 0
       }}]
  (Listen. (create-base-structure :structr-id structr-id
                                       :internal-strength internal-strength
                                       :external-strength external-strength
                                       :melody-fn get-listen-melody-event
                                       :post-play-fn sub-ensemble-status)
                 )
  )
