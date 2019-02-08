package ru.pushapp.scan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.Navigation;

public class GameFragment extends Fragment implements View.OnClickListener{

    Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        btn = view.findViewById(R.id.testbtn);
        btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {

        //open keyboard
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_gameFragment_to_winFragment);
    }
}
