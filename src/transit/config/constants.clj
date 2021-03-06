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

(ns transit.config.constants
  (:require
   [overtone.live :refer [midi->hz MIDI-RANGE]]
   )
  )

(def SAVED-MELODY-LEN 64)

(def FREE 0)
(def METERED 1)

(def MIN-NOTE (first MIDI-RANGE))
(def MAX-NOTE (last MIDI-RANGE))
(def MIN-FREQ (midi->hz (first MIDI-RANGE)))
(def MAX-FREQ (midi->hz (last MIDI-RANGE)))
