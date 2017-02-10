package com.example.kirti_pc.droidar.listeners;

import listeners.eventManagerListeners.CamRotationVecUpdateListener;
import listeners.eventManagerListeners.LocationEventListener;
import listeners.eventManagerListeners.OrientationChangedListener;
import listeners.eventManagerListeners.TouchMoveListener;
import listeners.eventManagerListeners.TrackBallEventListener;

@Deprecated
public interface EventListener extends LocationEventListener,
		OrientationChangedListener, TouchMoveListener, TrackBallEventListener,
		CamRotationVecUpdateListener {

}