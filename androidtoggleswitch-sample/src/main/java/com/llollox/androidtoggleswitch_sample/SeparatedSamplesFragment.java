package com.llollox.androidtoggleswitch_sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.llollox.androidtoggleswitch_sample.databinding.FragmentSeparatedSamplesBinding;


/**
 * Created by lorenzorigato on 22/06/2017.
 */

public class SeparatedSamplesFragment extends BaseSamplesFragment {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final FragmentSeparatedSamplesBinding binding = FragmentSeparatedSamplesBinding.inflate(inflater);

    binding.operatorsSeparatedToggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
      @Override
      public void onToggleSwitchChanged(int position) {
        String[] operators = getResources().getStringArray(R.array.operators);
        showToggleChangeToast(operators, position);
      }
    });

    binding.planetsSeparatedCircleToggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
      @Override
      public void onToggleSwitchChanged(int position) {
        String[] operators = getResources().getStringArray(R.array.planets);
        showToggleChangeToast(operators, position);
      }
    });


    return binding.getRoot();
  }
}
