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

(ns transit.player.player-utils
  (:require
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.melody.pitch :refer [select-random-pitch]]
   [transit.melody.rhythm :refer [select-random-rhythm]]
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
                       :structr-id nil
                       )
  )

(defn create-nodur-rest-melody-event
  [player-id event-id]
  (create-melody-event :melody-event-id event-id
                       :note nil
                       :dur-info nil
                       :volume nil
                       :instrument-info nil
                       :player-id player-id
                       :event-time nil
                       :structr-id nil
                       )
  )

(defn select-random-pitch-for-player
  ([player]
   (select-random-pitch (:range-lo (:instrument-info player))
                        (:range-hi (:instrument-info player))
                        )
   )
  ([player lo hi]
   (select-random-pitch (:range-lo (:instrument-info player))
                        (:range-hi (:instrument-info player))
                        )
   )
  )
