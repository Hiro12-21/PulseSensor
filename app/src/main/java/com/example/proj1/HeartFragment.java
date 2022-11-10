package com.example.proj1;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.mypackage.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeartFragment extends Fragment {
    TextView myLabel;
    TextView ppgLabel;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    static GraphView ppgGraph = null;
    private static LineGraphSeries<DataPoint> series = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart, container, false);

        myLabel = (TextView) view.findViewById(R.id.hrValue);
        ppgLabel = (TextView) view.findViewById(R.id.ppgValue);

        Button btConnectButton = (Button) view.findViewById(R.id.trackHRButton);
        btConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }});

        // ---- *** GRAPH **** ---- //
        ConstraintLayout ConstraintLayout = view.findViewById(R.id.ConstraintLayout);

        ppgGraph = (GraphView) view.findViewById(R.id.ppgGraph);
        series = new LineGraphSeries<DataPoint>();
        ppgGraph.addSeries(series);

        //chk if need
        ppgGraph.getViewport().setMinX(-500);
        ppgGraph.getViewport().setMinY(500);

        //scrolling and scaling
        ppgGraph.getViewport().setScrollable(true);
        ppgGraph.getViewport().setScrollableY(true);
        ppgGraph.getViewport().setScalable(true);
        ppgGraph.getViewport().setScalableY(true);

        ppgGraph.getGridLabelRenderer().setNumVerticalLabels(10);
        ppgGraph.getGridLabelRenderer().setNumHorizontalLabels(10);
        ppgGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        //chk if using getActivity() is correct
        ppgGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), new SimpleDateFormat("mm:ss")));
        return view;
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-05")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    //data starts coming in here
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    //unpackaging data
                                    String[] arrOfStr = data.split("_", 2);
                                    for (String ppg : arrOfStr)
                                        System.out.println(ppg);
                                    String ppgData = arrOfStr[0];
                                    String bpmData = arrOfStr[1];
                                    Log.d("Input data:", data); //visualise data

                                    //for graphview x-axis
                                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                                    final Date d1 = new Date(ts.getTime());
                                    final double ppg_int = Integer.valueOf(ppgData); //check if needed

                                    handler.post(new Runnable() {
                                        public void run() {
                                            myLabel.setText(bpmData);
                                            ppgLabel.setText(ppgData);
                                            series.appendData(new DataPoint(d1, ppg_int), true, 100, false);

                                            //String UID = FirebaseAuth.getInstance().getCurrentUser().getUid(); //this works but no threshold
                                            //FirebaseDatabase.getInstance().getReference("User Information").child(UID).child("bpm").setValue(bpmData);

                                            int bpmDataInt = Integer.parseInt(bpmData.trim());
                                            int threshold = 0;

                                            if(bpmDataInt == 0) { //can tweak threshold
                                                String HighHR = String.valueOf(bpmDataInt);

                                            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            FirebaseDatabase.getInstance().getReference("User Information").child(UID).child("bpm").setValue(HighHR);

                                            FirebaseDatabase.getInstance().getReference("User Information").child(UID).child("bpm").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // This method is called once with the initial value and again
                                                    // whenever data at this location is updated.
                                                    String value = dataSnapshot.getValue(String.class);
                                                    Log.d(TAG, "Heart rate is:" + value);
                                                    notification();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    // Failed to read value
                                                    Log.w(TAG, "Safe heart rate", error.toException());
                                                }
                                            });
                                            }
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    private void showMessage(String theMsg) {
        Toast msg = Toast.makeText(getActivity().getBaseContext(),
                theMsg, (Toast.LENGTH_LONG)/160);
        msg.show();
    }

    private void notification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("n","n", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(),"n")
                .setContentText("AID REQUIRED")
                .setSmallIcon(R.drawable.productlogo_old)
                .setAutoCancel(true)
                .setContentText("Heart stop detected!");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
        managerCompat.notify(999, builder.build());
    }
}

