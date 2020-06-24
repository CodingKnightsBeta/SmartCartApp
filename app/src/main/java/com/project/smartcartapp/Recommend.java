package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Recommend extends AppCompatActivity {

    private DatabaseReference ListRef;
    private DataSnapshot listData;
    public ArrayList<ArrayList<String>> listItems = new ArrayList<>();
    public int msc = 2;
    public double confidence = 0.6;
    public HashMap<ArrayList,Integer> items = new HashMap<>();
    public ArrayList<String> candidates = new ArrayList<>();
    public ArrayList<ArrayList<String>> newSet = new ArrayList<>();
    public ArrayList<ArrayList<String>> assoc = new ArrayList<>();

    public Map <ArrayList,ArrayList> rules = new HashMap<>();
    public Map <String,Object> finalRules = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        ListRef = FirebaseDatabase.getInstance().getReference().child("Shopping Lists");
        RecommendApriori();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void RecommendApriori(){
        FirebaseDatabase database = ListRef.getDatabase();

        ListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot allLists: dataSnapshot.getChildren()){
                    for(DataSnapshot userLists: allLists.getChildren()){
                        ArrayList<String> pList = new ArrayList<>();
                        for(DataSnapshot list: userLists.child("products").getChildren()){
                            pList.add(""+list.getKey());
//                            Log.d("key",""+list.getKey());
                        }
                        Collections.sort(pList);
                        for (String item: pList) {
                            if(!candidates.contains(item)){
                                candidates.add(item);
                            }
                        }
                        listItems.add(pList);
                    }
                }
                Collections.sort(candidates);
                msc = (int)(listItems.size() * 0.25);
                Log.d("data msc",""+listItems.size()+" "+msc);
                Log.d("item list",""+candidates);
                for(int n=2;!candidates.isEmpty();n++){
                    items.clear();
                    newSet.clear();

                    permute(candidates,new LinkedList<>(),items,candidates.size()-n+1,n,-1);
                    candidates.clear();

                    items.keySet().forEach(k -> {
                        listItems.stream().filter(j -> j.containsAll(k)).forEach( m -> items.put(k,items.get(k)+1));
                    });
                    items.keySet().stream().filter(b -> items.get(b) >=msc).forEach(b -> newSet.add(b));

                    if(!newSet.isEmpty()){
                        candidates.clear();
                        newSet.stream().forEach( a -> {
                            a.stream().filter(b->!candidates.contains(b)).forEach( c -> candidates.add(c));
                        });
                    }
                    if(!candidates.isEmpty()){
                        Log.d("data Items : " ,""+items);
                        Log.d("data Candidate items: ",""+newSet);
                        Log.d("data Next items : ",""+newSet);
                        assoc = (ArrayList<ArrayList<String>>)newSet.clone();
                    }
                    Log.d("item list end loop",""+candidates);

                }
                Log.d("data candidate",""+candidates);
                Log.d("data assoc",""+assoc);
                Log.d("data ListItems",""+items);

                for(ArrayList<String> cand: assoc){
//                    Log.d("data assoc",""+cand);
                    HashMap<ArrayList,Integer> testRules = new HashMap<>();
                    rules = getRules(cand);
                    rules.keySet().stream().forEach(c -> testRules.put(c,0));
                    rules.keySet().stream().forEach(a -> {
                        listItems.stream().filter(b -> b.containsAll(a)).forEach(m -> testRules.put(a,testRules.get(a)+1));
                    });
                    Log.d("datasupport",""+testRules);

                    int candSupport = (int)listItems.stream().filter(a -> a.containsAll(cand)).count();
                    testRules.keySet().stream().filter(b -> ((double)candSupport/(double)testRules.get(b)) >= confidence).forEach(m -> {
                        finalRules.put(m.toString(),rules.get(m).toString());
                        Log.d("dataConfidence",m+"..."+(double)candSupport/(double)testRules.get(m));
                    });
                }
                Log.d("dataRules",""+finalRules);
                addRules();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void permute(ArrayList next,LinkedList l,Map items,int size,int n,int it){
        for(int i=it+1;i<size;i++){
            l.add(i);
            if(n==1){
                ArrayList alist = new ArrayList();
                l.forEach(k -> alist.add(next.get((int)(k))));
                items.put(alist,0);
            }
            else  permute(next,l,items,size+1,n-1,i);
            l.pollLast();
        }
    }

    public static HashMap<ArrayList,ArrayList> getRules(ArrayList<String> candidates){
        HashMap <ArrayList,ArrayList> tmpRules = new HashMap<>();
        HashMap<String,String> map = new HashMap<>();

        Collections.sort(candidates);

        for(int i=0;i<candidates.size();i++) {
            ArrayList a = new ArrayList();
            ArrayList b = new ArrayList();

            b = (ArrayList)candidates.clone();
            a.add(candidates.get(i));
            b.remove(candidates.get(i));
            Collections.sort(a);
            Collections.sort(b);

            map.putIfAbsent(b+"",a+"");
            map.putIfAbsent(a+"",b+"");

            while(b.size()>1){
                a.add(b.get(0));
                b.remove(0);
                Collections.sort(a);
                Collections.sort(b);

                map.putIfAbsent(b+"",a+"");
                map.putIfAbsent(a+"",b+"");
            }
            map.keySet().stream().forEach(k -> {
                String r = k.replace("[", "").replace("]", "");
                String s = map.get(k).replace("[", "").replace("]", "");
                tmpRules.put(new ArrayList(Arrays.asList(r.split(", "))),new ArrayList(Arrays.asList(s.split(", "))));
            });
        }
        return tmpRules;
    }

    public void addRules() {
        Log.d("dataSendRules",""+finalRules);
        DatabaseReference rulesRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> associations = new HashMap<>();
        rulesRef.child("Rules").removeValue();

        finalRules.keySet().forEach(b -> associations.put(b.toString().replace("[", "").replace("]", ""),finalRules.get(b).toString().replace("[", "").replace("]", "")));
        rulesRef.child("Rules").updateChildren(associations).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Recommend.this,"Rules Added",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
