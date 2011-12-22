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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.blork.safetrip.util.Debug;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapRouteFragment extends Fragment {

	public static final String SERVICE = "http://rezippy.com/safe/route.php";
	private MapView mapView;
	public ArrayList<Step> steps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.map, container, false);

		mapView = (MapView)view.findViewById(R.id.myMapView);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

		MapOverlay mapOverlay = new MapOverlay(SafeTripActivity.routes.get(getShownRoute()), Color.BLUE);
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();

		Debug.log(getShownRoute() + " route, step: "+getShownStep());

		Button next = (Button)view.findViewById(R.id.buttonNext);
		Button prev = (Button)view.findViewById(R.id.buttonPrev);

		final MapController mapController = mapView.getController();

		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				mapController.animateTo(steps.get(moveToNextStep()).getPoints().get(0));
				getSupportActivity().getSupportActionBar().setSubtitle(
						SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).getHtmlDescription().toString()
						);

			}
		});
		prev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				mapController.animateTo(steps.get(moveToPrevStep()).getPoints().get(0));
				getSupportActivity().getSupportActionBar().setSubtitle(
						SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).getHtmlDescription().toString()
						);

			}
		});

		getSupportActivity().getSupportActionBar().setSubtitle(
				SafeTripActivity.routes.get(getShownRoute()).getSteps().get(getShownStep()).getHtmlDescription().toString()
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
		private int color;

		public MapOverlay(Route mRoute, int color) {
			this.mRoute = mRoute;
			this.color = color;
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
			linePaint.setColor(color);
			linePaint.setAntiAlias(true);
			linePaint.setStrokeJoin(Paint.Join.ROUND); 
			linePaint.setStrokeCap(Paint.Cap.ROUND);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(5);

			Paint circlePaint = new Paint();
			circlePaint.setColor(Color.BLACK);
			circlePaint.setAntiAlias(true);
			circlePaint.setStrokeJoin(Paint.Join.ROUND); 
			circlePaint.setStrokeCap(Paint.Cap.ROUND);
			circlePaint.setStyle(Paint.Style.FILL);
			circlePaint.setStrokeWidth(1);


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

					if (j == 0) {
						canvas.drawCircle(x2, y2, 5, circlePaint);
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
}