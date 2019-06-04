package pipelineStages;

import reservationStations.ReservationStations;

public class Execute extends Stage{
	
	ReservationStations reservationStations;
	
	public Execute(ReservationStations reservationStations) {
		this.reservationStations = reservationStations;
	}

	@Override
	public void run() {
		this.reservationStations.decrementTime();
	}
	
}
