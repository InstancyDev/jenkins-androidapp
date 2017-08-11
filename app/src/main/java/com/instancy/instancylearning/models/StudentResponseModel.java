package com.instancy.instancylearning.models;

import java.io.Serializable;

public class StudentResponseModel implements Serializable {
    private String _siteId;
    private int _scoId;
    private int _userId;
    private int _questionid;
    private int _assessmentattempt;
    private int _questionattempt;
    private String _attemptdate = "";
    private String _studentresponses = "";
    private String _attachfilename = "";
    private String _attachfileid = "";
    private String _attachedfilepath = "";
    private String _optionalNotes = "";
    private String _result;
    private int _rindex;
    private String _capturedVidFileName = "";
    private String _capturedVidId = "";
    private String _capturedVidFilepath = "";
    private String _capturedImgFileName = "";
    private String _capturedImgId = "";
    private String _capturedImgFilepath = "";


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

    public int get_questionid() {
        return _questionid;
    }

    public void set_questionid(int _questionid) {
        this._questionid = _questionid;
    }

    public int get_assessmentattempt() {
        return _assessmentattempt;
    }

    public void set_assessmentattempt(int _assessmentattempt) {
        this._assessmentattempt = _assessmentattempt;
    }

    public int get_questionattempt() {
        return _questionattempt;
    }

    public void set_questionattempt(int _questionattempt) {
        this._questionattempt = _questionattempt;
    }

    public String get_attemptdate() {
        return _attemptdate;
    }

    public void set_attemptdate(String _attemptdate) {
        this._attemptdate = _attemptdate;
    }

    public String get_studentresponses() {
        return _studentresponses != null ? _studentresponses : "";
    }

    public void set_studentresponses(String _studentresponses) {
        this._studentresponses = _studentresponses;
    }

    public String get_attachfilename() {
        return _attachfilename != null ? _attachfilename : "";
    }

    public void set_attachfilename(String _attachfilename) {
        this._attachfilename = _attachfilename;
    }

    public String get_attachfileid() {
        return _attachfileid != null ? _attachfileid : "";
    }

    public void set_attachfileid(String _attachfileid) {
        this._attachfileid = _attachfileid;
    }

    public String get_attachedfilepath() {
        return _attachedfilepath;
    }

    public void set_attachedfilepath(String _attachedfilepath) {
        this._attachedfilepath = _attachedfilepath;
    }

    public String get_result() {
        return _result;
    }

    public void set_result(String _result) {
        this._result = _result;
    }

    public int get_rindex() {
        return _rindex;
    }

    public void set_rindex(int _rindex) {
        this._rindex = _rindex;
    }

    public String get_optionalNotes() {
        return _optionalNotes != null ? _optionalNotes : "";
    }

    public void set_optionalNotes(String _optionalNotes) {
        this._optionalNotes = _optionalNotes;
    }

    public String get_capturedVidFileName() {
        return _capturedVidFileName;
    }

    public void set_capturedVidFileName(String _capturedVidFileName) {
        this._capturedVidFileName = _capturedVidFileName;
    }

    public String get_capturedVidId() {
        return _capturedVidId != null ? _capturedVidId : "";
    }

    public void set_capturedVidId(String _capturedVidId) {
        this._capturedVidId = _capturedVidId;
    }

    public String get_capturedVidFilepath() {
        return _capturedVidFilepath;
    }

    public void set_capturedVidFilepath(String _capturedVidFilepath) {
        this._capturedVidFilepath = _capturedVidFilepath;
    }


    public String get_capturedImgFileName() {
        return _capturedImgFileName != null ? _capturedImgFileName : "";
    }

    public void set_capturedImgFileName(String _capturedImgFileName) {
        this._capturedImgFileName = _capturedImgFileName;
    }

    public String get_capturedImgId() {
        return _capturedImgId != null ? _capturedImgId : "";
    }

    public void set_capturedImgId(String _capturedImgId) {
        this._capturedImgId = _capturedImgId;
    }

    public String get_capturedImgFilepath() {
        return _capturedImgFilepath;
    }

    public void set_capturedImgFilepath(String _capturedImgFilepath) {
        this._capturedImgFilepath = _capturedImgFilepath;
    }

}
