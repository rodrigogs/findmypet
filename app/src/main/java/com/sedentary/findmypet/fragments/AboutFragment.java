package com.sedentary.findmypet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.base.utils.IntentUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AboutFragment extends Fragment {

	@InjectView(R.id.logo_imageview) ImageView mLogoImageView;
	@InjectView(R.id.facebook_button) TextView mFacebookButton;
	@InjectView(R.id.git_button) TextView mGitButton;
	@InjectView(R.id.blog_button) TextView mBlogButton;
	@InjectView(R.id.popcorn_button) TextView mPopcornButton;
	@InjectView(R.id.discuss_button) TextView mDiscussButton;
	@InjectView(R.id.twitter_button) TextView mTwitterButton;

	public AboutFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_about, container, false);
	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}


	@OnClick(R.id.logo_imageview) void onLogoClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.example.com"));
	}

	@OnClick(R.id.facebook_button) void onFacebookClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.facebook.com"));
	}

	@OnClick(R.id.git_button) void onGitClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.github.com"));
	}

	@OnClick(R.id.blog_button) void onBlogClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.myblog.com"));
	}

	@OnClick(R.id.popcorn_button) void onPopcornClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.mywebsite.com"));
	}

	@OnClick(R.id.discuss_button) void onDiscussClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.myforum.com"));
	}

	@OnClick(R.id.twitter_button) void onTwitterClick() {
		startActivity(IntentUtils.getBrowserIntent(getActivity(), "www.twitter.com"));
	}

}
