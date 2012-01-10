package com.blork.safetrip;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blork.safetrip.util.Debug;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapRouteFragment extends Fragment {

	public static final String SERVICE = "http://rezippy.com/safe/route.php";
	private MapView mapView;
	public ArrayList<Step> steps;
	private MapController mapController;
	private TextView directionText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.map, container, false);

		mapView = (MapView)view.findViewById(R.id.myMapView);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

		MapOverlay mapOverlay = new MapOverlay(SafeTripActivity.routes.get(getShownRoute()));
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();

		Debug.log(getShownRoute() + " route, step: "+getShownStep());


		mapController = mapView.getController();

		directionText = (TextView)view.findViewById(R.id.directionTextView);

		directionText.setSelected(true);
		directionText.setText(
				SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).toString()
				);

		return view;

	}

	public static MapRouteFragment newInstance(int route, int step) {
		Debug.log("The route passed to newInstance is " + route);
		MapRouteFragment f = new MapRouteFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("step", step);
		args.putInt("route", route);

		f.setArguments(args);

		return f;
	}

	public int moveToNextStep() {
		int step = getArguments().getInt("step", 0);
		if (step < steps.size()-1) {
			step++;
			getArguments().putInt("step", step);
		}
		return step;
	}

	public int moveToPrevStep() {
		int step = getArguments().getInt("step", 0);
		if (step > 0) {
			step--;
			getArguments().putInt("step", step);
		}
		return step;
	}

	public int getShownStep() {
		return getArguments().getInt("step", 0);
	}

	public int getShownRoute() {
		return getArguments().getInt("route", 0);
	}


	class MapOverlay extends com.google.android.maps.Overlay {

		private Route mRoute;

		public MapOverlay(Route mRoute) {
			this.mRoute = mRoute;
			steps = mRoute.getSteps();
			if (steps.size() > 0) {
				MapController mapController = mapView.getController();
				//				mapController.zoomToSpan(
				//						steps.get(0).getPoints().get(0).getLatitudeE6() - 
				//						steps.get(steps.size()-1).getPoints().get(steps.get(steps.size()-1).getPoints().size()-1).getLatitudeE6(),
				//						steps.get(0).getPoints().get(0).getLongitudeE6() - 
				//						steps.get(steps.size()-1).getPoints().get(steps.get(steps.size()-1).getPoints().size()-1).getLongitudeE6()
				//						);
				mapController.setZoom(16);
				mapController.animateTo(steps.get(getShownStep()).getPoints().get(0));

			}
		}

		@Override
		public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
			super.draw(canvas, mv, shadow);
			drawPath(mv, canvas);
			return true;
		}

		public void drawPath(MapView mv, Canvas canvas) {
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			Paint linePaint = new Paint();
			linePaint.setColor(Color.parseColor("#3366FF"));
			linePaint.setAntiAlias(true);
			linePaint.setStrokeJoin(Paint.Join.ROUND); 
			linePaint.setStrokeCap(Paint.Cap.ROUND);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(7);

			Paint circlePaint = new Paint();
			circlePaint.setColor(Color.GREEN);
			circlePaint.setAntiAlias(true);
			circlePaint.setStrokeJoin(Paint.Join.ROUND); 
			circlePaint.setStrokeCap(Paint.Cap.ROUND);
			circlePaint.setStyle(Paint.Style.FILL);
			circlePaint.setStrokeWidth(1);

			Paint borderPaint = new Paint();
			borderPaint.setColor(Color.BLACK);
			borderPaint.setAntiAlias(true);
			borderPaint.setStrokeJoin(Paint.Join.ROUND); 
			borderPaint.setStrokeCap(Paint.Cap.ROUND);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setStrokeWidth(1);

			ArrayList<Step> steps = mRoute.getSteps();

			for (int i = 0; i < steps.size(); i++) {
				Step step = steps.get(i);
				List<GeoPoint> points = step.getPoints();
				for (int j = 0; j < points.size(); j++) {
					Point point = new Point();
					mv.getProjection().toPixels(points.get(j), point);
					x2 = point.x;
					y2 = point.y;

					if (j > 0) {
						canvas.drawLine(x1, y1, x2, y2, linePaint);
					} 



					x1 = x2;
					y1 = y2;

				}

			}

			for (int i = 0; i < steps.size(); i++) {
				Step step = steps.get(i);
				List<GeoPoint> points = step.getPoints();
				for (int j = 0; j < points.size(); j++) {
					Point point = new Point();
					mv.getProjection().toPixels(points.get(j), point);
					x2 = point.x;
					y2 = point.y;

					if (j == 0) {
						canvas.drawCircle(x2, y2, 10, circlePaint);
						canvas.drawCircle(x2, y2, 10, borderPaint);

					}



					x1 = x2;
					y1 = y2;

				}

			}

			//TODO: optimise these. geopoints can be gotten easier
			Point point = new Point();
			mv.getProjection().toPixels(steps.get(0).getPoints().get(0), point);

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.start);
			canvas.drawBitmap(bmp, point.x-16, point.y-35, null);

			point = new Point();
			mv.getProjection().toPixels(steps.get(steps.size()-1).getPoints().get(steps.get(steps.size()-1).getPoints().size()-1), point);

			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.stop);
			canvas.drawBitmap(bmp, point.x-16, point.y-35, null);
		}

	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_next:
			mapController.animateTo(steps.get(moveToNextStep()).getPoints().get(0));
			directionText.setText(
					SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).toString()
					);
			return true;
		case R.id.menu_prev:
			mapController.animateTo(steps.get(moveToPrevStep()).getPoints().get(0));
			directionText.setText(
					SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).toString()
					);
		case android.R.id.home:
			return true;
		}

		return false;		
	}
}