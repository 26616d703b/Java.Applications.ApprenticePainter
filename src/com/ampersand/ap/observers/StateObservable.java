package com.ampersand.ap.observers;

public interface StateObservable {

	public void addObserver(StateObserver observer);

	public void notifyObserver(boolean saved);

	public void removeObserver();
}
