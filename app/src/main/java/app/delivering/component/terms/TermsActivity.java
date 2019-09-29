package app.delivering.component.terms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.terms.TermsAndConditionsInitBinder;

public class TermsActivity extends BaseActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        TermsAndConditionsInitBinder initBinder = new TermsAndConditionsInitBinder(this);
        addItemForViewsInjection(initBinder);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
