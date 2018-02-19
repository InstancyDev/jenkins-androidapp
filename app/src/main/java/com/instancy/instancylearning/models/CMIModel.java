package com.instancy.instancylearning.models;

import java.io.Serializable;

public class CMIModel implements Serializable {

    private int _Id;
    private String _siteId="";
    private int _scoId;
    private int _userId;
    private String _location = "";
    private String _status = "";
    private String _suspenddata = "";
    private String _isupdate = "";
    private String _sitrurl = "";
    private String _objecttypeid = "";
    private String _datecompleted = "";
    private int _noofattempts = 0;
    private String _score = "";
    private String _seqNum = "";
    private String _startdate = "";
    private String _timespent = "";
    private String _attemptsleft = "";
    private String _coursemode = "";
    private String _scoremin = "";
    private String _scoremax = "";
    private String _submittime = "";
    private int _trackscoid;
    private String _qusseq = "";
    private String _pooledqusseq = "";
    private String _textResponses = "";



    public String getParentObjTypeId() {
        return parentObjTypeId;
    }

    public void setParentObjTypeId(String parentObjTypeId) {
        this.parentObjTypeId = parentObjTypeId;
    }

    public String getParentContentId() {
        return parentContentId;
    }

    public void setParentContentId(String parentContentId) {
        this.parentContentId = parentContentId;
    }

    public String getParentScoId() {
        return parentScoId;
    }

    public void setParentScoId(String parentScoId) {
        this.parentScoId = parentScoId;
    }

    private String parentObjTypeId = "";
    private String parentContentId = "";
    private String parentScoId = "";

    public String get_contentId() {
        return _contentId;
    }

    public void set_contentId(String _contentId) {
        this._contentId = _contentId;
    }

    private String _contentId = "";

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    private String showStatus = "";


    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }

    public String get_siteId() {
        return _siteId;
    }

    public void set_siteId(String _siteId) {
        this._siteId = _siteId;
    }

    public int get_scoId() {
        return _scoId;
    }

    public void set_scoId(int _scoId) {
        this._scoId = _scoId;
    }

    public int get_userId() {
        return _userId;
    }

    public void set_userId(int _userId) {
        this._userId = _userId;
    }

    public String get_location() {
        return _location;
    }

    public void set_location(String _location) {
        this._location = _location;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public String get_suspenddata() {
        return _suspenddata;
    }

    public void set_suspenddata(String _suspenddata) {
        this._suspenddata = _suspenddata;
    }

    public String get_isupdate() {
        return _isupdate;
    }

    public void set_isupdate(String _isupdate) {
        this._isupdate = _isupdate;
    }

    public String get_sitrurl() {
        return _sitrurl;
    }

    public void set_sitrurl(String _sitrurl) {
        this._sitrurl = _sitrurl;
    }

    public String get_datecompleted() {
        return _datecompleted;
    }

    public void set_datecompleted(String _datecompleted) {
        this._datecompleted = _datecompleted;
    }

    public int get_noofattempts() {
        return _noofattempts;
    }

    public void set_noofattempts(int _noofattempts) {
        this._noofattempts = _noofattempts;
    }

    public String get_score() {
        return _score;
    }

    public void set_score(String _score) {
        this._score = _score;
    }

    public String get_objecttypeid() {
        return _objecttypeid;
    }

    public void set_objecttypeid(String _objecttypeid) {
        this._objecttypeid = _objecttypeid;
    }

    public String get_seqNum() {
        return _seqNum;
    }

    public void set_seqNum(String _seqNum) {
        this._seqNum = _seqNum;
    }

    public String get_startdate() {
        return _startdate;
    }

    public void set_startdate(String _startdate) {
        this._startdate = _startdate;
    }

    public String get_timespent() {
        return _timespent;
    }

    public void set_timespent(String _timespent) {
        this._timespent = _timespent;
    }

    public String get_attemptsleft() {
        return _attemptsleft;
    }

    public void set_attemptsleft(String _attemptsleft) {
        this._attemptsleft = _attemptsleft;
    }

    public String get_coursemode() {
        return _coursemode;
    }

    public void set_coursemode(String _coursemode) {
        this._coursemode = _coursemode;
    }

    public String get_scoremin() {
        return _scoremin;
    }

    public void set_scoremin(String _scoremin) {
        this._scoremin = _scoremin;
    }

    public String get_scoremax() {
        return _scoremax;
    }

    public void set_scoremax(String _scoremax) {
        this._scoremax = _scoremax;
    }

    public String get_submittime() {
        return _submittime;
    }

    public void set_submittime(String _submittime) {
        this._submittime = _submittime;
    }

    public int get_trackscoid() {
        return _trackscoid;
    }

    public void set_trackscoid(int _trackscoid) {
        this._trackscoid = _trackscoid;
    }

    public String get_qusseq() {
        return _qusseq;
    }

    public void set_qusseq(String _qusseq) {
        this._qusseq = _qusseq;
    }

    public String get_pooledqusseq() {
        return _pooledqusseq;
    }

    public void set_pooledqusseq(String _pooledqusseq) {
        this._pooledqusseq = _pooledqusseq;
    }

    public String get_textResponses() {
        return _textResponses;
    }

    public void set_textResponses(String _textResponses) {
        this._textResponses = _textResponses;
    }
}
