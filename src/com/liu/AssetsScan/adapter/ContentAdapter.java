package com.liu.AssetsScan.adapter;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.capitalcode.assetsystemmobile.BaseActivity;
import com.capitalcode.assetsystemmobile.R;
import com.capitalcode.assetsystemmobile.ShowImageActivity;
import com.capitalcode.assetsystemmobile.SimpleSelectActivity;
import com.liu.AssetsScan.util.Address;
import com.liu.AssetsScan.util.ImageFactory;
import com.zhy.tree_view.ComplexSelectActivity;

import android.util.Log;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentAdapter extends android.widget.BaseAdapter {

	static final int DATE_DIALOG_ID = 0;
	String address = null;
	String address2 = null;
	private int mRequestCode;

	public static final class EditHolder {
		public TextView nameTextView;
		public EditText valueEditText;
	}

	public static final class ChooseHolder {
		public TextView nameTextView;
		public TextView valueTextView;
		public Button clearbtn;
	}

	public static final class AddressHolder {
		public TextView nameTextView;
		public EditText valueEditText;
		public Button clearbtn;
	}

	public static final class AddPicHolder {
		public TextView nameTextView;
		public Button clearbtn;
	}

	public static final class PicHolder {
		public ImageView valueImageView;
		public Button clearbtn;
	}

	BaseActivity activity;
	Map<String, String> PageLabel;
	private Context context;
	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;

	public ContentAdapter(Context context, List<Map<String, Object>> list, Map<String, String> PageLabel) {
		activity = (BaseActivity) context;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.PageLabel = PageLabel;
	}

	public ContentAdapter(Context context, List<Map<String, Object>> list) {
		activity = (BaseActivity) context;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;

	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return (long) arg0;
	}

	private Integer index = -1;

	@Override
	public View getView(final int arg0, View convertView, ViewGroup arg2) {
		HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);
		String type = (String) map.get("type");
		Log.v("grq", "type的值为" + type);
		String readonly = (String) map.get("readonly");

		String value = (String) map.get("value");

		String name = (String) map.get("name");

		if (PageLabel != null && name == null) {
			String id = (String) map.get("id");
			if (id != null) {
				@SuppressWarnings("rawtypes")
				Iterator iter = PageLabel.entrySet().iterator();
				while (iter.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					if (id.equals(key.toString())) {

						name = PageLabel.get(id);
						Log.d("grq", "判断力的第一个name=" + name + "   " + PageLabel.get(id));
					}

				}
				if (name == null) {
					Log.d("grq", "name的值任然没有改变" + name);
				}
			}

		} else {

			name = (String) map.get("name");
			Log.d("grq", "否定判断name=" + name);
		}

		EditHolder editHolder = null;
		ChooseHolder chooseHolder = null;
		AddressHolder addressHolder = null;
		AddPicHolder addHolder = null;
		PicHolder picHolder = null;
		int flag = 1;
		if (type.equals("edit")) {

			if (convertView == null) {
				editHolder = new EditHolder();
				convertView = this.mInflater.inflate(R.layout.item_edit, null);

				editHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
				editHolder.valueEditText = ((EditText) convertView.findViewById(R.id.value));
				Log.d("grq", "if (if) type 判断name=" + name + (flag++));
				editHolder.valueEditText.setTag(arg0);
				convertView.setTag(editHolder);
			} else {
				Object object = convertView.getTag();
				if (object.getClass().equals(EditHolder.class) == false) {
					editHolder = new EditHolder();
					convertView = this.mInflater.inflate(R.layout.item_edit, null);

					editHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
					editHolder.valueEditText = ((EditText) convertView.findViewById(R.id.value));

					editHolder.valueEditText.setTag(arg0);

					convertView.setTag(editHolder);
					Log.d("grq", "else (if) type 判断name=" + name + (flag++));
				} else {
					editHolder = (EditHolder) object;
					editHolder.valueEditText.setTag(arg0);
					Log.d("grq", "else (else) type 判断name=" + name + (flag++));
				}
			}
			Log.e("grq", "nameTextView1=" + name);
			editHolder.nameTextView.setText(name);

			final Integer imeOptions = (Integer) map.get("imeOptions");
			if (imeOptions != null) {
				// editHolder.valueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				editHolder.valueEditText.setImeOptions(imeOptions);
				editHolder.valueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				editHolder.valueEditText.setSingleLine(true);

				final EditText edit = editHolder.valueEditText;

				Log.e("setImeOptionssetImeOptionssetImeOptions", "setImeOptionssetImeOptionssetImeOptions");

				editHolder.valueEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

						if (actionId == imeOptions) {
							activity.onSearch();

							edit.setText("");
							// edit.selectAll();

							InputMethodManager imm = (InputMethodManager) context
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

							return true;
						}
						return false;
					}

				});

			} else {
				editHolder.valueEditText.setImeOptions(EditorInfo.IME_ACTION_NONE);
				editHolder.valueEditText.setSingleLine(false);
				Log.e("nonononosetImeOptionssetImeOptionssetImeOptions",
						"nononononosetImeOptionssetImeOptionssetImeOptions");
				Log.d("grq", "else imeOptions 判断name=" + name + (flag++));
			}

			String inputtype = (String) map.get("inputtype");
			if (inputtype != null) {
				editHolder.valueEditText.setInputType(Integer.valueOf(inputtype));
				Log.d("input_type", inputtype);
				Log.d("grq", "if inputtype 判断name=" + name + (flag++));
			} else {
				editHolder.valueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				Log.d("grq", "else inputtype 判断name=" + name + (flag++));
			}

			editHolder.valueEditText.addTextChangedListener(new MyTextWatcher(editHolder) {
				@Override
				public void afterTextChanged(Editable s, EditHolder holder) {
					int position = (Integer) holder.valueEditText.getTag();
					list.get(position).put("value", s.toString());// 当EditText数据发生改变的时候存到data变量中
					list.get(position).put("realvalue", s.toString());// 当EditText数据发生改变的时候存到data变量中

				}
			});

			editHolder.valueEditText.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = arg0;
					}
					return false;
				}
			});

			editHolder.valueEditText.setText(value);

			if (index == arg0) {
				editHolder.valueEditText.requestFocus();
			} else {

				editHolder.valueEditText.clearFocus();

			}

			if (readonly == null) {
				editHolder.valueEditText.setEnabled(true);
			} else {
				editHolder.valueEditText.setEnabled(false);
			}

		} else if (type.equals("address")) {

			if (convertView == null) {
				addressHolder = new AddressHolder();
				convertView = this.mInflater.inflate(R.layout.item_address, null);

				addressHolder.nameTextView = ((TextView) convertView.findViewById(R.id.address_name));
				addressHolder.valueEditText = ((EditText) convertView.findViewById(R.id.address_value));
				addressHolder.clearbtn = ((Button) convertView.findViewById(R.id.address_btn_clear));
				addressHolder.valueEditText.setTag(arg0);
				convertView.setTag(addressHolder);
			} else {
				Object object = convertView.getTag();
				if (object.getClass().equals(AddressHolder.class) == false) {
					addressHolder = new AddressHolder();
					convertView = this.mInflater.inflate(R.layout.item_address, null);

					addressHolder.nameTextView = ((TextView) convertView.findViewById(R.id.address_name));
					addressHolder.valueEditText = ((EditText) convertView.findViewById(R.id.address_value));
					addressHolder.clearbtn = ((Button) convertView.findViewById(R.id.address_btn_clear));
					addressHolder.valueEditText.setTag(arg0);
					convertView.setTag(addressHolder);

				} else {
					addressHolder = (AddressHolder) object;
					addressHolder.valueEditText.setTag(arg0);
				}

			}
			Log.e("grq", "nameTextView2=" + name);
			addressHolder.nameTextView.setText(name);

			//在这里传入value的值
			if(Address.getAddress()!=null){
				address = Address.getAddress();
			}
			addressHolder.valueEditText.addTextChangedListener(new AddTextWatcher(addressHolder) {
				@Override
				public void afterTextChanged(Editable s, AddressHolder holder) {

					int position = (Integer) holder.valueEditText.getTag();
					if(address!=null){
						list.get(position).put("value", address);
						list.get(position).put("realvalue", address);

					}else{
						list.get(position).put("value", s.toString());// 当EditText数据发生改变的时候存到data变量中
						list.get(position).put("realvalue", s.toString());// 当EditText数据发生改变的时候存到data变量中
					}
				}
			});
			
			if(address!=null){
				addressHolder.valueEditText.setText(address);
			}else{
				addressHolder.valueEditText.setText(value);
			}

			Log.i("value", "value="+value.toString());
			
			address2 = addressHolder.valueEditText.getText().toString();
			Log.i("address2", "address2="+address2);
			addressHolder.clearbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//百度地图定位
//					Intent intent = new Intent(activity,LocationDemo.class);
//					//					context.startActivity(intent);
//					Log.i("address", "address="+address2);
//					if(address2!=null){
//						intent.putExtra("address", address2);
//					}
//					activity.startActivityForResult(intent, 5);
				}
			});
			
			address = null;
			Address.setAddress(null);

		} else if (type.equals("choose")) {

			if (convertView == null) {
				chooseHolder = new ChooseHolder();
				convertView = this.mInflater.inflate(R.layout.item_choose, null);

				chooseHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
				chooseHolder.valueTextView = ((TextView) convertView.findViewById(R.id.value));
				chooseHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_clear));

				convertView.setTag(chooseHolder);
			} else {
				Object object = convertView.getTag();
				if (object.getClass().equals(ChooseHolder.class) == false) {
					chooseHolder = new ChooseHolder();
					convertView = this.mInflater.inflate(R.layout.item_choose, null);

					chooseHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
					chooseHolder.valueTextView = ((TextView) convertView.findViewById(R.id.value));
					chooseHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_clear));

					convertView.setTag(chooseHolder);

				} else {
					chooseHolder = (ChooseHolder) object;

				}

			}
			Log.e("grq", "nameTextView2=" + name);
			chooseHolder.nameTextView.setText(name);
			chooseHolder.valueTextView.setText(value);

			Log.d("grq123", "valueTextView的值   " + chooseHolder.valueTextView.getText());

			chooseHolder.valueTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);
					String searchid = (String) map.get("searchid");
					if (searchid == null) {
						return;
					}

					if (searchid.equals("datepicker")) {
						Calendar cal = Calendar.getInstance();

						final DatePickerDialog mDialog = new DatePickerDialog(context, null, cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

						mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
							@SuppressLint("NewApi")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
								DatePicker datePicker = mDialog.getDatePicker();
								int year = datePicker.getYear();
								int month = datePicker.getMonth() + 1;
								int day = datePicker.getDayOfMonth();

								String strMonth = null;
								if (month < 10) {
									strMonth = "0" + month;

								} else {
									strMonth = month + "";
								}

								String strDay = null;
								if (day < 10) {
									strDay = "0" + day;

								} else {
									strDay = day + "";
								}

								HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);
								map.put("value", year + "-" + strMonth + "-" + strDay);
								map.put("realvalue", year + "-" + strMonth + "-" + strDay);

								ContentAdapter.this.notifyDataSetChanged();
							}
						});

						mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

						mDialog.show();

					} else if (searchid.equals("useUser") == true) {
						String title = null;
						String id = (String) map.get("id");
						Boolean onlyone = (Boolean) map.get("onlyone");
						if (PageLabel != null) {

							title = PageLabel.get(id);
						} else {
							title = (String) map.get("name");
						}

						Intent intent = new Intent(context, ComplexSelectActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("searchid", searchid);
						intent.putExtra("realvalue", (String) map.get("realvalue"));

						ComplexSelectActivity.item = map;
						ComplexSelectActivity.updateadapter = ContentAdapter.this;

						ComplexSelectActivity.items.clear();

						if (onlyone == null) {
							for (Map<String, Object> one : list) {
								String depid = (String) one.get("id");
								if (id.equals("useUserId") && depid.equals("useDeptId")) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("DrawUserId") && (depid.equals("DrawDeptId")
										|| depid.equals("Int150") || depid.equals("Int240"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("RegUserId") && (depid.equals("RegDeptId"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("ScrapUserId") && (depid.equals("ScrapDeptId"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("RetUserId") && (depid.equals("RetDeptId"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("BorUserId") && (depid.equals("BorDeptId"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("Int190") && (depid.equals("Int200"))) {
									ComplexSelectActivity.items.add(one);
								} else if (id.equals("ReceUserId")
										&& (depid.equals("ReceDeptId") || depid.equals("ManageDeptId"))) {
									ComplexSelectActivity.items.add(one);
								}
							}
						}

						context.startActivity(intent);

					} else if (searchid.equals("useDept") == true) {
						String title = null;

						if (PageLabel != null) {
							String id = (String) map.get("id");
							title = PageLabel.get(id);
						} else {
							title = (String) map.get("name");
						}

						Intent intent = new Intent(context, ComplexSelectActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("searchid", searchid);
						intent.putExtra("realvalue", (String) map.get("realvalue"));

						ComplexSelectActivity.item = map;
						ComplexSelectActivity.updateadapter = ContentAdapter.this;

						context.startActivity(intent);
					} else if (searchid.equals("category") == true) {
						String title = null;

						if (PageLabel != null) {
							String id = (String) map.get("id");
							title = PageLabel.get(id);
						} else {
							title = (String) map.get("name");
						}

						Intent intent = new Intent(context, ComplexSelectActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("searchid", searchid);
						intent.putExtra("realvalue", (String) map.get("realvalue"));

						ComplexSelectActivity.item = map;
						ComplexSelectActivity.updateadapter = ContentAdapter.this;

						context.startActivity(intent);
					}else {

						String title = null;

						if (PageLabel != null) {
							String id = (String) map.get("id");
							title = PageLabel.get(id);
						} else {
							title = (String) map.get("name");
						}

						String value = (String) map.get("value");

						Intent intent = new Intent(context, SimpleSelectActivity.class);

						intent.putExtra("title", title);
						intent.putExtra("value", value);
						intent.putExtra("searchid", searchid);

						SimpleSelectActivity.item = map;
						SimpleSelectActivity.updateadapter = ContentAdapter.this;
						context.startActivity(intent);
					}

				}

			});

			chooseHolder.clearbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);
					map.put("value", "");
					map.put("realvalue", null);
					ContentAdapter.this.notifyDataSetChanged();
				}

			});

			if (readonly == null) {
				chooseHolder.clearbtn.setVisibility(View.VISIBLE);
				chooseHolder.valueTextView.setClickable(true);
			} else {
				chooseHolder.clearbtn.setVisibility(View.GONE);
				chooseHolder.valueTextView.setClickable(false);
			}

		} else if (type.equals("addpic")) {
			if (convertView == null) {
				addHolder = new AddPicHolder();
				convertView = this.mInflater.inflate(R.layout.item_btn, null);

				addHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
				addHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_add_pic));

				convertView.setTag(addHolder);
			} else {
				Object object = convertView.getTag();
				if (object.getClass().equals(AddPicHolder.class) == false) {
					addHolder = new AddPicHolder();
					convertView = this.mInflater.inflate(R.layout.item_btn, null);

					addHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
					addHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_add_pic));

					convertView.setTag(addHolder);
				} else {
					addHolder = (AddPicHolder) object;

				}

			}
			Log.e("grq", "nameTextView3=" + name);
			addHolder.nameTextView.setText(name);

			addHolder.clearbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					String filePath = getFileName();
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));

					activity.startActivityForResult(intent, 3);

				}

			});

			if (readonly == null) {
				addHolder.clearbtn.setEnabled(true);
			} else {
				addHolder.clearbtn.setEnabled(false);
			}

		} else if (type.equals("pic")) {

			if (convertView == null) {
				picHolder = new PicHolder();
				convertView = this.mInflater.inflate(R.layout.item_image, null);

				picHolder.valueImageView = ((ImageView) convertView.findViewById(R.id.pic));
				picHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_clear));

				convertView.setTag(picHolder);
			} else {
				Object object = convertView.getTag();
				if (object.getClass().equals(PicHolder.class) == false) {
					picHolder = new PicHolder();
					convertView = this.mInflater.inflate(R.layout.item_image, null);

					picHolder.valueImageView = ((ImageView) convertView.findViewById(R.id.pic));
					picHolder.clearbtn = ((Button) convertView.findViewById(R.id.btn_clear));

					convertView.setTag(picHolder);
				} else {
					picHolder = (PicHolder) object;
				}
			}

			Bitmap bm = (Bitmap) map.get("image");
			
			if (bm != null) {
				
				ImageFactory factory = new ImageFactory();
				Bitmap bitmap = factory.ratio(bm, 240f, 120f);
				picHolder.valueImageView.setImageBitmap(bitmap);
			}

			picHolder.clearbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);
					list.remove(arg0);
					ContentAdapter.this.notifyDataSetChanged();
				}

			});

			picHolder.valueImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					HashMap<String, Object> map = (HashMap<String, Object>) list.get(arg0);

					Intent intent = new Intent(context, ShowImageActivity.class);
					ShowImageActivity.base64 = (String) map.get("data");
					context.startActivity(intent);

				}

			});

			if (readonly == null) {
				picHolder.clearbtn.setVisibility(View.VISIBLE);
			} else {
				picHolder.clearbtn.setVisibility(View.INVISIBLE);
			}

		}

		return convertView;

	}

	abstract class MyTextWatcher implements TextWatcher {
		private EditHolder mHolder;

		public MyTextWatcher(EditHolder holder) {
			this.mHolder = holder;
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			afterTextChanged(s, mHolder);
		}

		public abstract void afterTextChanged(Editable s, EditHolder holder);
	}

	abstract class AddTextWatcher implements TextWatcher {
		private AddressHolder mHolder;

		public AddTextWatcher(AddressHolder holder) {
			this.mHolder = holder;
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			afterTextChanged(s, mHolder);
		}

		public abstract void afterTextChanged(Editable s, AddressHolder holder);
	}

	private String getFileName() {
		String saveDir = Environment.getExternalStorageDirectory() + "/myPic";
		File dir = new File(saveDir);
		if (!dir.exists()) {
			dir.mkdir(); // 创建文件夹
		}

		String fileName = saveDir + "/" + "temp.jpg";

		return fileName;
	}
}
