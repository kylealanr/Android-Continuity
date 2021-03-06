package com.bottlerocketstudios.continuitysample.legislator.serialization;

import com.bottlerocketstudios.continuitysample.core.util.JSONUtil;
import com.bottlerocketstudios.continuitysample.legislator.model.Legislator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Automatically generated by jsonGenerator.py
 */
public class LegislatorSerializer {
    private static final String TAG = LegislatorSerializer.class.getSimpleName();
    
    private static final String OBJECT_NAME = "resultsItem";
        
    private static final String FIELD_LAST_NAME = "last_name";
    private static final String FIELD_STATE_NAME = "state_name";
    private static final String FIELD_OFFICE = "office";
    private static final String FIELD_FAX = "fax";
    private static final String FIELD_THOMAS_ID = "thomas_id";
    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_MIDDLE_NAME = "middle_name";
    private static final String FIELD_DISTRICT = "district";
    private static final String FIELD_SENATE_CLASS = "senate_class";
    private static final String FIELD_IN_OFFICE = "in_office";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_TERM_END = "term_end";
    private static final String FIELD_CRP_ID = "crp_id";
    private static final String FIELD_OC_EMAIL = "oc_email";
    private static final String FIELD_PARTY = "party";
    private static final String FIELD_FEC_IDS = "fec_ids";
    private static final String FIELD_VOTESMART_ID = "votesmart_id";
    private static final String FIELD_WEBSITE = "website";
    private static final String FIELD_LIS_ID = "lis_id";
    private static final String FIELD_GOVTRACK_ID = "govtrack_id";
    private static final String FIELD_FACEBOOK_ID = "facebook_id";
    private static final String FIELD_ICPSR_ID = "icpsr_id";
    private static final String FIELD_BIOGUIDE_ID = "bioguide_id";
    private static final String FIELD_BIRTHDAY = "birthday";
    private static final String FIELD_TERM_START = "term_start";
    private static final String FIELD_NICKNAME = "nickname";
    private static final String FIELD_CONTACT_FORM = "contact_form";
    private static final String FIELD_OCD_ID = "ocd_id";
    private static final String FIELD_PHONE = "phone";
    private static final String FIELD_GENDER = "gender";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_NAME_SUFFIX = "name_suffix";
    private static final String FIELD_TWITTER_ID = "twitter_id";
    private static final String FIELD_CHAMBER = "chamber";
    private static final String FIELD_STATE_RANK = "state_rank";
    private static final String FIELD_YOUTUBE_ID = "youtube_id";
    
    public static String getObjectName() {
        return OBJECT_NAME;
    }
    
    public static Legislator parseJsonObject(JSONObject json, LegislatorImageUrlFormatter legislatorImageUrlFormatter) throws JSONException {
        
        Legislator legislator = new Legislator();

        legislator.setLastName(json.getString(FIELD_LAST_NAME));
        legislator.setStateName(json.getString(FIELD_STATE_NAME));
        legislator.setOffice(json.getString(FIELD_OFFICE));
        legislator.setFax(JSONUtil.optString(json, FIELD_FAX, null));
        legislator.setThomasId(json.getString(FIELD_THOMAS_ID));
        legislator.setFirstName(json.getString(FIELD_FIRST_NAME));
        legislator.setMiddleName(JSONUtil.optString(json, FIELD_MIDDLE_NAME, null));
        legislator.setDistrict(json.optLong(FIELD_DISTRICT));
        legislator.setSenateClass(json.optLong(FIELD_SENATE_CLASS));
        legislator.setInOffice(json.getBoolean(FIELD_IN_OFFICE));
        legislator.setState(json.getString(FIELD_STATE));
        legislator.setTermEnd(json.getString(FIELD_TERM_END));
        legislator.setCrpId(json.getString(FIELD_CRP_ID));
        legislator.setOcEmail(json.getString(FIELD_OC_EMAIL));
        legislator.setParty(json.getString(FIELD_PARTY));

        JSONArray fecIdsJsonArray = json.getJSONArray(FIELD_FEC_IDS);
        List<String> fecIds = new ArrayList<>(fecIdsJsonArray.length());
        for (int i = 0; i < fecIdsJsonArray.length(); i++) {
            fecIds.add(fecIdsJsonArray.getString(i));
        }
        legislator.setFecIds(fecIds);

        legislator.setVotesmartId(json.optLong(FIELD_VOTESMART_ID));
        legislator.setWebsite(json.getString(FIELD_WEBSITE));
        legislator.setLisId(JSONUtil.optString(json, FIELD_LIS_ID, null));
        legislator.setGovtrackId(json.getString(FIELD_GOVTRACK_ID));
        legislator.setFacebookId(JSONUtil.optString(json, FIELD_FACEBOOK_ID, null));
        legislator.setIcpsrId(json.optLong(FIELD_ICPSR_ID));
        legislator.setBioguideId(json.getString(FIELD_BIOGUIDE_ID));
        legislator.setBirthday(json.getString(FIELD_BIRTHDAY));
        legislator.setTermStart(json.getString(FIELD_TERM_START));
        legislator.setNickname(JSONUtil.optString(json, FIELD_NICKNAME, null));
        legislator.setContactForm(JSONUtil.optString(json, FIELD_CONTACT_FORM, null));
        legislator.setOcdId(json.getString(FIELD_OCD_ID));
        legislator.setPhone(json.getString(FIELD_PHONE));
        legislator.setGender(json.getString(FIELD_GENDER));
        legislator.setTitle(json.getString(FIELD_TITLE));
        legislator.setNameSuffix(JSONUtil.optString(json, FIELD_NAME_SUFFIX, null));
        legislator.setTwitterId(json.getString(FIELD_TWITTER_ID));
        legislator.setChamber(json.getString(FIELD_CHAMBER));
        legislator.setStateRank(JSONUtil.optString(json, FIELD_STATE_RANK, null));
        legislator.setYoutubeId(JSONUtil.optString(json, FIELD_YOUTUBE_ID, null));

        //Custom attribute
        legislator.setImageUrl(legislatorImageUrlFormatter.getImageUrl(legislator.getGovtrackId()));

        return legislator;
    }

    public static JSONObject toJsonObject(Legislator serializableObject) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(FIELD_LAST_NAME, serializableObject.getLastName());
        obj.put(FIELD_STATE_NAME, serializableObject.getStateName());
        obj.put(FIELD_OFFICE, serializableObject.getOffice());
        obj.put(FIELD_FAX, serializableObject.getFax());
        obj.put(FIELD_THOMAS_ID, serializableObject.getThomasId());
        obj.put(FIELD_FIRST_NAME, serializableObject.getFirstName());
        obj.put(FIELD_MIDDLE_NAME, serializableObject.getMiddleName());
        obj.put(FIELD_DISTRICT, serializableObject.getDistrict());
        obj.put(FIELD_SENATE_CLASS, serializableObject.getSenateClass());
        obj.put(FIELD_IN_OFFICE, serializableObject.isInOffice());
        obj.put(FIELD_STATE, serializableObject.getState());
        obj.put(FIELD_TERM_END, serializableObject.getTermEnd());
        obj.put(FIELD_CRP_ID, serializableObject.getCrpId());
        obj.put(FIELD_OC_EMAIL, serializableObject.getOcEmail());
        obj.put(FIELD_PARTY, serializableObject.getParty());

        JSONArray fecIdsJsonArray = new JSONArray();
        List<String> fecIds = serializableObject.getFecIds();
        for (String item : fecIds) {
            fecIdsJsonArray.put(item);
        }
        obj.put(FIELD_FEC_IDS, fecIdsJsonArray);

        obj.put(FIELD_VOTESMART_ID, serializableObject.getVotesmartId());
        obj.put(FIELD_WEBSITE, serializableObject.getWebsite());
        obj.put(FIELD_LIS_ID, serializableObject.getLisId());
        obj.put(FIELD_GOVTRACK_ID, serializableObject.getGovtrackId());
        obj.put(FIELD_FACEBOOK_ID, serializableObject.getFacebookId());
        obj.put(FIELD_ICPSR_ID, serializableObject.getIcpsrId());
        obj.put(FIELD_BIOGUIDE_ID, serializableObject.getBioguideId());
        obj.put(FIELD_BIRTHDAY, serializableObject.getBirthday());
        obj.put(FIELD_TERM_START, serializableObject.getTermStart());
        obj.put(FIELD_NICKNAME, serializableObject.getNickname());
        obj.put(FIELD_CONTACT_FORM, serializableObject.getContactForm());
        obj.put(FIELD_OCD_ID, serializableObject.getOcdId());
        obj.put(FIELD_PHONE, serializableObject.getPhone());
        obj.put(FIELD_GENDER, serializableObject.getGender());
        obj.put(FIELD_TITLE, serializableObject.getTitle());
        obj.put(FIELD_NAME_SUFFIX, serializableObject.getNameSuffix());
        obj.put(FIELD_TWITTER_ID, serializableObject.getTwitterId());
        obj.put(FIELD_CHAMBER, serializableObject.getChamber());
        obj.put(FIELD_STATE_RANK, serializableObject.getStateRank());
        obj.put(FIELD_YOUTUBE_ID, serializableObject.getYoutubeId());
        return obj;
    }
   
}

