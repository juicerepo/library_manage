package com.gondor.isildur.service;

import com.alibaba.fastjson.JSONObject;
import com.gondor.isildur.entity.Admin;

public interface AdminService extends BaseService<Admin, Long> {

  JSONObject login(JSONObject params);

  JSONObject create(Admin admin);

  JSONObject put(long adminId, Admin admin);

  JSONObject getByPage(int page, int size, String adminName);

  //JSONObject delete(long adminId);

  // public JSONObject getCount();

  // JSONObject get(long adminId);

  // JSONObject getByPage(int page);
}