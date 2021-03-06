package com.yvelabs.timerecording;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.yvelabs.timerecording.utils.DateUtils;
import com.yvelabs.timerecording.utils.LogUtils;
import com.yvelabs.timerecording.utils.MyKeyValuePair;
import com.yvelabs.timerecording.utils.SpinnerUtils;
import com.yvelabs.timerecording.utils.TypefaceUtils;
import com.yvelabs.timerecording.utils.SpinnerUtils.MySpinnerAdapter2;

public class RecordHistorySearchDialog extends DialogFragment {
	
	private TextView titleTextTv;
	private Spinner eventNameSp;
	private Spinner eventCategoryNameSp;
	private TextView startEventDateTv;
	private TextView endeventDateTv;
	private ImageButton searchBut;
	private ImageButton eventDateClearBut;
	
	private ArrayAdapter<MyKeyValuePair> eventSppinerAdapter;
	private List<MyKeyValuePair> eventSpinnerList = new ArrayList<MyKeyValuePair>();
	private EventRecordModel parameter = new EventRecordModel();
	private Date startEventDate;
	private Date endEventDate;
	
	static RecordHistorySearchDialog newInstance() {
		RecordHistorySearchDialog f = new RecordHistorySearchDialog();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.record_history_search_dialog, null);
		titleTextTv = (TextView) view.findViewById(R.id.record_history_search_dialog_title_text);
		eventNameSp = (Spinner) view.findViewById(R.id.record_history_search_dialog_event_name);
		eventCategoryNameSp = (Spinner) view.findViewById(R.id.record_history_search_dialog_event_category_name);
		searchBut = (ImageButton) view.findViewById(R.id.record_history_search_dialog_search_but);
		startEventDateTv = (TextView) view.findViewById(R.id.record_history_search_dialog_event_date_from);
		endeventDateTv = (TextView) view.findViewById(R.id.record_history_search_dialog_event_date_to);
		eventDateClearBut = (ImageButton) view.findViewById(R.id.record_history_search_dialog_event_date_clear_but);
		
		startEventDateTv.setKeyListener(null);
		startEventDateTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				
				Dialog dialog = new DatePickerDialog(getActivity(),
						new DatePickerDialog.OnDateSetListener() {
							public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
								startEventDateTv.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
								
								startEventDate = DateUtils.getDateByYMD(year, month, dayOfMonth);
							}
						}, 
						c.get(Calendar.YEAR), // 传入年份
						c.get(Calendar.MONTH), // 传入月份
						c.get(Calendar.DAY_OF_MONTH) // 传入天数
				);
				
				dialog.show();
			}
		});
		
		endeventDateTv.setKeyListener(null);
		endeventDateTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				Dialog dialog = new DatePickerDialog(getActivity(),
						new DatePickerDialog.OnDateSetListener() {
							public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
								endeventDateTv.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
								endEventDate = DateUtils.getDateByYMD(year, month, dayOfMonth);
							}
						}, 
						c.get(Calendar.YEAR), // 传入年份
						c.get(Calendar.MONTH), // 传入月份
						c.get(Calendar.DAY_OF_MONTH) // 传入天数
						
				);
				
				dialog.show();
			}
		});
		
		new TypefaceUtils().setTypeface(titleTextTv, TypefaceUtils.MOBY_MONOSPACE);
		
		List<MyKeyValuePair> categoryList = new ArrayList<MyKeyValuePair>();
		categoryList.add(new MyKeyValuePair(null, ""));
		categoryList.addAll(new SpinnerUtils().categorySpinner(getActivity()));
		ArrayAdapter<MyKeyValuePair> categorySppinerAdapter = new SpinnerUtils().new MySpinnerAdapter2(getActivity(), categoryList); 
		eventCategoryNameSp.setAdapter(categorySppinerAdapter);
		MyKeyValuePair categoryPair = (MyKeyValuePair) eventCategoryNameSp.getSelectedItem();
		
		eventSpinnerList.add(new MyKeyValuePair(null, ""));
		eventSpinnerList.addAll(new SpinnerUtils().eventSpinner(getActivity(), categoryPair.getKey() == null ? null : categoryPair.getKey().toString()));
		eventSppinerAdapter = new SpinnerUtils().new MySpinnerAdapter2(getActivity(), eventSpinnerList);
		eventNameSp.setAdapter(eventSppinerAdapter);
		
		eventCategoryNameSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				MyKeyValuePair categoryPair = (MyKeyValuePair)eventCategoryNameSp.getSelectedItem();
				
				eventSpinnerList.removeAll(eventSpinnerList);
				eventSpinnerList.add(new MyKeyValuePair(null, ""));
				eventSpinnerList.addAll(new SpinnerUtils().eventSpinner(getActivity(), categoryPair.getKey() == null ? null : categoryPair.getKey().toString())); 
				eventSppinerAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		searchBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				MyKeyValuePair categoryPair = (MyKeyValuePair) eventCategoryNameSp.getSelectedItem();
				MyKeyValuePair eventPair = (MyKeyValuePair) eventNameSp.getSelectedItem();
				parameter.setEventName(eventPair.getKey() == null ? null : eventPair.getKey().toString());
				parameter.setEventCategoryName(categoryPair.getKey() == null ? null : categoryPair.getKey().toString());
				
				if (startEventDate != null) {
					parameter.setStartEventDate(startEventDate);
				}
				if (endEventDate != null) {
					parameter.setEndEventDate(endEventDate);
				}
				
				((RecordActivity)getActivity()).refreshHistoryList(parameter);
				getDialog().dismiss();
			}
		});
		
		eventDateClearBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startEventDate = null;
				endEventDate = null;
				
				startEventDateTv.setText("");
				endeventDateTv.setText("");
			}
		});
		
		return view;
	}

}
