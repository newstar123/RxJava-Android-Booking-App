package app.delivering.component.profile.adapter;

public class AgeGenderAdaptersModel {
    private SpinnerHintAdapter ageAdapter;
    private SpinnerHintAdapter genderAdapter;

    public SpinnerHintAdapter getGenderAdapter() {
        return genderAdapter;
    }

    public void setGenderAdapter(SpinnerHintAdapter genderAdapter) {
        this.genderAdapter = genderAdapter;
    }

    public SpinnerHintAdapter getAgeAdapter() {
        return ageAdapter;
    }

    public void setAgeAdapter(SpinnerHintAdapter ageAdapter) {
        this.ageAdapter = ageAdapter;
    }
}
