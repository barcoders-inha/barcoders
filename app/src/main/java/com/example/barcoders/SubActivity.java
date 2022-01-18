package com.example.barcoders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        List<Barcode> ItemList = initLoadBarcodeDatabase();

        String[] arr = addItem(ItemList);

        if (arr[1].equals("metal")){
            Intent intent = new Intent(SubActivity.this,Success_metal.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else if (arr[1].equals("paper")){
            Intent intent = new Intent(SubActivity.this,Success_paper.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else if (arr[1].equals("poly")){
            Intent intent = new Intent(SubActivity.this,Success_poly.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else if (arr[1].equals("glass")){
            Intent intent = new Intent(SubActivity.this,Success_glass.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else if (arr[1].equals("vinyl")){
            Intent intent = new Intent(SubActivity.this,Success_vinyl.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else if (arr[1].equals("plastic")){
            Intent intent = new Intent(SubActivity.this,Success_plastic.class);
            intent.putExtra("name",arr[0]);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SubActivity.this,Fail.class);
            startActivity(intent);
        }

    }


    public List<Barcode> initLoadBarcodeDatabase(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.OpenDatabaseFile();

        List<Barcode> ItemList = databaseHelper.getTableData();

        databaseHelper.close();

        return ItemList;
    }

    public String[] addItem(List<Barcode> ItemList) {

        //String num = "8801075012071";
        String num = getIntent().getStringExtra("number");

        String[] arr = {"item","material"};

        Log.e("test",String.valueOf(ItemList.size()));

        for (int i = 0; i < ItemList.size(); i++) {

            if (num.equals(ItemList.get(i).getBarcode())) {
                arr[0] = ItemList.get(i).getItem();
                arr[1] = ItemList.get(i).getMaterial();
                break;
            }

        }
        return arr;
    }

}
