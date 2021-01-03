/**
 * Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.chromium.chrome.browser.onboarding;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import static org.chromium.ui.base.ViewUtils.dpToPx;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import org.chromium.base.Log;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.onboarding.OnboardingPrefManager;
import org.chromium.chrome.browser.onboarding.SearchEngineEnum;
import org.chromium.chrome.browser.search_engines.TemplateUrlServiceFactory;
import org.chromium.chrome.browser.settings.BraveSearchEngineUtils;
import org.chromium.components.search_engines.TemplateUrl;
import org.chromium.components.search_engines.TemplateUrlService;

import java.util.List;

public class SearchEngineOnboardingFragment extends Fragment {
    private String searchSpanText = "%s\n%s";
    private RadioGroup radioGroup;

    private Button btnSave;

    private TemplateUrl selectedSearchEngine;

    public SearchEngineOnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search_engine_onboarding, container, false);

        initializeViews(root);

        setActions();

        refreshData();

        return root;
    }

    private void refreshData() {
        TemplateUrlService templateUrlService = TemplateUrlServiceFactory.get();
        List<TemplateUrl> templateUrls = templateUrlService.getTemplateUrls();
        TemplateUrl defaultSearchEngineTemplateUrl =
            BraveSearchEngineUtils.getTemplateUrlByShortName(BraveSearchEngineUtils.getDSEShortName(false));

        for (TemplateUrl templateUrl : templateUrls) {
            if (templateUrl.getIsPrepopulated()
                    && OnboardingPrefManager.searchEngineMap.get(templateUrl.getShortName())
                    != null) {
                SearchEngineEnum searchEngineEnum =
                    OnboardingPrefManager.searchEngineMap.get(templateUrl.getShortName());

                String title = templateUrl.getShortName();
                String desc = getActivity().getResources().getString(searchEngineEnum.getDesc());

                SpannableString searchTextSpan =
                        new SpannableString(String.format(searchSpanText, title, desc));
                searchTextSpan.setSpan(new AbsoluteSizeSpan(16, true), 0, title.length(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
                searchTextSpan.setSpan(
                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                        title.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                searchTextSpan.setSpan(new AbsoluteSizeSpan(12, true), title.length() + 1,
                        searchTextSpan.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                RadioButton rdBtn = new RadioButton(getActivity());
                rdBtn.setId(searchEngineEnum.getId());
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT, dpToPx(getActivity(), 64));
                params.setMargins(0, dpToPx(getActivity(), 6), 0, 0);
                rdBtn.setLayoutParams(params);
                rdBtn.setButtonDrawable(null);
                rdBtn.setPadding(dpToPx(getActivity(), 30), 0, 0, 0);
                rdBtn.setTextColor(
                        getActivity().getResources().getColor(R.color.onboarding_text_color));
                rdBtn.setBackgroundDrawable(getActivity().getResources().getDrawable(
                        R.drawable.radiobutton_background));
                rdBtn.setText(searchTextSpan);
                rdBtn.setCompoundDrawablesWithIntrinsicBounds(
                        getActivity().getResources().getDrawable(searchEngineEnum.getIcon()), null,
                        null, null);
                rdBtn.setCompoundDrawablePadding(dpToPx(getActivity(), 16));
                radioGroup.addView(rdBtn);
            }
        }

        if (defaultSearchEngineTemplateUrl != null
                && OnboardingPrefManager.searchEngineMap.get(
                    defaultSearchEngineTemplateUrl.getShortName())
                != null)
            radioGroup.check(OnboardingPrefManager.searchEngineMap
                             .get(defaultSearchEngineTemplateUrl.getShortName())
                             .getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                searchEngineSelected(index, templateUrls);
            }
        });
    }

    private void initializeViews(View root) {
        radioGroup = root.findViewById(R.id.radio_group);

        btnSave = root.findViewById(R.id.btn_save);
    }

    private void setActions() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSearchEngine == null) {
                    selectedSearchEngine = BraveSearchEngineUtils.getTemplateUrlByShortName(BraveSearchEngineUtils.getDSEShortName(false));
                }
                if (selectedSearchEngine != null) {
                    BraveSearchEngineUtils.setDSEPrefs(selectedSearchEngine, false);
                    BraveSearchEngineUtils.setDSEPrefs(selectedSearchEngine, true);
                }
                getActivity().finish();
            }
        });
    }

    private void searchEngineSelected(int position, List<TemplateUrl> templateUrls) {
        selectedSearchEngine = templateUrls.get(position);
    }
}