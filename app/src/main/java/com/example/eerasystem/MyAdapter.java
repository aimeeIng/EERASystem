package com.example.eerasystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ServiceHolder> implements Filterable {

    private Context context;
    private List<Employee> list;
    private ArrayList<Employee> listAll;

    public MyAdapter(Context context , List<Employee> employees){
        this.context = context;
        list = employees;
        this.listAll = new ArrayList<>(list);
    }

    public void setFilteredList(List<Employee> filteredList){
        this.list = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_design , parent , false);
        return new ServiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {

        Employee emp = list.get(position);
        holder.title.setText(emp.getTitle());
        holder.overview.setText(emp.getDescription());
        holder.delete.setOnClickListener(v->{
            deleteEmployee(emp.getId(),position);

        });
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , EmployeeActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title" , emp.getTitle());
                bundle.putString("overview" , emp.getDescription());

                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    private void deleteEmployee(int empId,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject params = new JSONObject();
                try{
                    params.put("id",empId+" ");
                } catch(JSONException e){
                    e.printStackTrace();
                }
                String data = params.toString();
                String url = "http://192.168.1.69/db_delete.php";
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                JSONObject object = new JSONObject();
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                listAll.clear();
                                listAll.addAll(list);
                                Toast toast = Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }).start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ServiceHolder extends RecyclerView.ViewHolder{

        ImageView delete;
        TextView title , overview;
        ConstraintLayout constraintLayout;

        public ServiceHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_tv);

            delete = itemView.findViewById(R.id.delete);
            overview = itemView.findViewById(R.id.overview_tv);
            constraintLayout = itemView.findViewById(R.id.main_layout);
        }
    }

    }



