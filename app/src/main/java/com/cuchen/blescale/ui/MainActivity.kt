package com.cuchen.blescale.ui


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import com.cuchen.blescale.DeviceData
import com.cuchen.blescale.R
import com.cuchen.blescale.ble.BleManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*setContent {
            BleScaleTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(topBar = {
                    ScaleAppBar { manager ->
                        bleManager = manager
                    }
                },
                    modifier = Modifier.fillMaxSize()*//*, color = MaterialTheme.colorScheme.background*//*,
                    content = {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
//                            items(scanSnapshotList) { topic ->
//                                Log.d("scanSnapshotList", topic.name)
//                            }
                        }
                        Greeting("Android")*//*    innerPadding ->
                        Text(
                            text = "Body content",
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .wrapContentSize()
                        )*//*
                    }) *//*{

                    *//**//*BodyContent(
                        Greeting("Android")
                    )*//**//*
                }*//*
            }
        }*/

        if (Build.VERSION.SDK_INT >= 31) {
            if (permissionArray.all {
                    ContextCompat.checkSelfPermission(
                        this, it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                Toast.makeText(this, "권한 확인", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissionLauncher.launch(permissionArray)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }

    /* @Composable
     fun ScaleAppBar(bleState: (BleManager) -> Unit) {
         val checkedScan = remember { mutableStateOf(false) }
 //        val context = LocalContext.current
 //        val bleManager = BleManager(context)
         val scanList = remember { mutableStateListOf<DeviceData>() }
 //        scanSnapshotList = scanList
         bleManager.setScanList(scanList)
         TopAppBar(
             modifier = Modifier
                 .background(Color.Blue)
                 .requiredHeight(70.dp),

             title = {
                 Text(
                     style = TextStyle(color = Color.Blue),

                     modifier = Modifier
                         .fillMaxHeight()
                         .wrapContentSize(),
                     text = "취사 물량 저울"
                 )
             },
             actions = {
                 ScanBleBtn(
                     checked = checkedScan.value,
                     setScan = {
                         checkedScan.value = !checkedScan.value
                         println(" checkedScan.value : ${checkedScan.value}")
                         bleState(bleManager)

                         if (checkedScan.value) {
                             bleManager.startBleScan()

                         } else {
                             bleManager.stopBleScan()
                         }
                     },
                 )
             })
     }

     @Composable
     fun ScanBleBtn(
         setScan: () -> Unit,
         checked: Boolean
     ) {
         IconButton(
             onClick = { setScan() }, modifier = Modifier.fillMaxHeight()
         ) {
             if (checked) {
                 Icon(
                     painter = painterResource(
                         id = R.drawable.ic_launcher_foreground
                     ),
                     contentDescription = ""
                 )
             } else {
                 Icon(
                     painter = painterResource(
                         id = R.drawable.ic_launcher_background
                     ),
                     contentDescription = ""
                 )
             }
         }
     }


     @Composable
     fun Greeting(name: String, modifier: Modifier = Modifier) {
         Text(
             text = "Hello $name!", modifier = modifier
                 .fillMaxSize()
                 .wrapContentSize()
         )
     }*/

    /* @Preview(showBackground = true)
     @Composable
     fun GreetingPreview() {
         BleScaleTheme {
             Greeting("Android")
         }
     }*/
}

