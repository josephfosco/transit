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

(ns transit.player.structures.base-structure)

(defrecord BaseStructure [internal-strength
                          external-strength
                          created-at
                          updated-at])

(defn create-base-structure
  [& {:keys [internal-strength external-strength] :or
      {internal-strength 0 external-strength 0}}]
  (BaseStructure. internal-strength
                  external-strength
                  created-at (System/currentTimeMillis)
                  updated-at (System/currentTimeMillis)
                  )
  )

(defn get-base-strength
  [base-struct]
  (min 100 (+ (:internal-strength base-struct) (:external-strength base-struct)))
  )
