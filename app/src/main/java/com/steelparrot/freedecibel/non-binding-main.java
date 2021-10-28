//onCreate() {
//        //        setContentView(R.layout.activity_main);
////
////        mSearchText = (EditText) findViewById(R.id.search_text);
////        mSearchButton = (Button) findViewById(R.id.btn_search);
////        mSearchButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if(mSearchText.getText().toString().equals("")) {
////                    Toast.makeText(getApplicationContext(),"Insert your keywords first",Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                mProgressDialog = new ProgressDialog(MainActivity.this);
////                mProgressDialog.setMessage("Loading...");
////                mProgressDialog.show();
////
//////                // create handle for RetrofitInstance interface
////                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
////                HashMap<String, String> map = new HashMap<>();
////                map.put("query", mSearchText.getText().toString());
////                Call<List<YTItem>> call = service.getYTItems(map);
////                call.enqueue(new Callback<List<YTItem>>() {
////                    @Override
////                    public void onResponse(Call<List<YTItem>> call, Response<List<YTItem>> response) {
////                        switch (response.code()) {
////                            case 200:
////                                isData = true;
//////                                setContentView(R.layout.activity_main);
////                                mProgressDialog.dismiss();
////                                generateDataList(response.body());
////                                break;
////                            case 404:
////                                Toast.makeText(getApplicationContext(),"Couldn't find nothing on YT with these keywords!",Toast.LENGTH_SHORT).show();
////                                break;
////                            default:
////                                Toast.makeText(getApplicationContext(),"Server problem... Try again!",Toast.LENGTH_SHORT).show();
////                        }
////                    }
////
////                    @Override
////                    public void onFailure(Call<List<YTItem>> call, Throwable t) {
////                        Log.d("retrofit1", t.toString());
////                        mProgressDialog.dismiss();
////                        Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////        });
//}


// generateDataList() {
//        mRecyclerView = findViewById(R.id.customRecyclerView);
//        mAdapter = new CustomAdapter(this, dataList);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setAdapter(mAdapter);
//}