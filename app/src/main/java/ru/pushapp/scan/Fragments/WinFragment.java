package ru.pushapp.scan.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;
import ru.pushapp.scan.App;
import ru.pushapp.scan.R;

public class WinFragment extends Fragment implements View.OnClickListener {

    int index = 0;

    Button nextBtn;
    Button allLvlBtn;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.win_fragment, container, false);

        index = getArguments().getInt("id_scanword") + 1;
        String text = "Сканворд №" + index + "\nразгадан на 100%";
        textView = view.findViewById(R.id.result_tv);
        textView.setText(text);

        nextBtn = view.findViewById(R.id.next_lvl_btn);
        nextBtn.setOnClickListener(this);

        allLvlBtn = view.findViewById(R.id.all_lvl_btn);
        allLvlBtn.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_lvl_btn: {

                //todo
                Bundle bundle = new Bundle();
                bundle.putString("id_scanword", "scanword_" + (index + 1));
                if (index + 1 >= App.getCrosswordSize()) {
                    Navigation.findNavController(view).popBackStack(R.id.levelFragment, false);
                } else {
                    Navigation.findNavController(view).navigate(R.id.action_gameFragment_to_winFragment, bundle);
                }
                break;
            }
            case R.id.all_lvl_btn: {
                Navigation.findNavController(view).popBackStack(R.id.levelFragment, false);
                break;
            }
        }

    }
}
