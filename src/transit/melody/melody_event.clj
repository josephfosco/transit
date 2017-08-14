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

(ns transit.melody.melody-event)

(defrecord MelodyEvent [id note dur-info volume instrument-info player-id event-time play-time sc-instrument-id])

(defn create-melody-event
  [& {:keys [:id :note :dur-info :volume :instrument-info :player-id :event-time :play-time :sc-instrument-id]}]
  (MelodyEvent. id
                note
                dur-info
                volume
                instrument-info
                player-id
                event-time
                nil  ;; :play-time
                nil  ;; sc-instrument-id
                )
  )

(defn set-sc-instrument-id-and-times
  [melody-event sc-instrument-id event-time play-time]
  (assoc melody-event
         :event-time event-time
         :play-time play-time
         :sc-instrument-id sc-instrument-id
         ))

(defn get-instrument-info-from-melody-event
 [melody-event]
 (:instrument-info melody-event)
 )

(defn get-note-from-melody-event
 [melody-event]
 (:note melody-event)
 )

(defn get-volume-from-melody-event
 [melody-event]
 (:volume melody-event)
 )

(defn print-melody-event
  [melody-event]
  (let [inst-inf (:instrument-info melody-event)
        melody-map (into (sorted-map)
                         (assoc
                          melody-event
                          :instrument-info
                          {:name (:name (:instrument inst-inf))
                           :range-lo (:range-lo inst-inf)
                           :range-hi (:range-hi inst-inf)}
                          :dur-info
                          (into {} (:dur-info melody-event))
                          ))
        ]
    (println melody-map)
    )
  )
