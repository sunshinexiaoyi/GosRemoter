package gos.remoter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import gos.remoter.R;

public class LiverFragment extends Fragment {

    private int image;
    private String text;
    private String[] program;

    //Gridview的适配器
    private GridView mGridview;
    private ArrayAdapter<String> arrayAdapterGridview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_liver, container, false);

        ImageView imageView  = (ImageView) view.findViewById(R.id.liver_fragment_image);
        TextView textView  = (TextView) view.findViewById(R.id.liver_fragment_textview);
        GridView gridView  = (GridView) view.findViewById(R.id.liver_fragment_gridview);

        //得到数据
        image = getArguments().getInt("image");
        text = getArguments().getString("text");
        program = getArguments().getStringArray("program");

        //设置数据
        imageView.setImageResource(image);
        textView.setText(text);
        arrayAdapterGridview = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.liver_program_gridview_item, program);
        gridView.setAdapter(arrayAdapterGridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return view;
    }

}
