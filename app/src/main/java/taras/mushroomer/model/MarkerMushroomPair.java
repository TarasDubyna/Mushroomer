package taras.mushroomer.model;

import com.google.android.gms.maps.model.Marker;

public class MarkerMushroomPair {

    private Marker marker;
    private Mushroom mushroom;

    public MarkerMushroomPair() {
    }

    public MarkerMushroomPair(Marker marker, Mushroom mushroom) {
        this.marker = marker;
        this.mushroom = mushroom;
    }

    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Mushroom getMushroom() {
        return mushroom;
    }
    public void setMushroom(Mushroom mushroom) {
        this.mushroom = mushroom;
    }
}
