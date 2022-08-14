package com.yikekong.es;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.util.JsonUtil;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ESRepository {


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * Adding Devices
     * @param deviceDTO
     */
    public  void addDevices(DeviceDTO deviceDTO){
        if(deviceDTO==null ) return;
        if(deviceDTO.getDeviceId()==null) return;
        IndexRequest request=new IndexRequest("devices");
        try {
            String json = JsonUtil.serialize(deviceDTO);
            Map map = JsonUtil.getByJson(json, Map.class);
            request.source(map);
            request.id(deviceDTO.getDeviceId());
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Device addition exception occurs");
        }
    }


    /**
     * Query a device based on its id
     * @param deviceId  device id
     * @return
     */
    public DeviceDTO searchDeviceById(String deviceId){
        SearchRequest searchRequest=new SearchRequest("devices");
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id",deviceId));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            long hitsCount = hits.getTotalHits().value;
            if(hitsCount<=0) return null;
            DeviceDTO deviceDTO=null;
            for(SearchHit hit:hits){
                String hitResult = hit.getSourceAsString();
                deviceDTO=JsonUtil.getByJson(hitResult,DeviceDTO.class  );
                deviceDTO.setDeviceId(deviceId);
                break;
            }
            return deviceDTO;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Query device exceptions");
            return null;
        }
    }


    /**
     * update device status
     * @param deviceId
     * @param status
     * @return
     */
    public boolean updateStatus(String deviceId,Boolean status){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "status",status );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error updating device status");
            return false;
        }
    }


    /**
     * Update device tags
     * @param deviceId
     * @param tags
     * @return
     */
    public boolean updateDeviceTag(String deviceId,String tags){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "tag",tags );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error updating device tag");
            return false;
        }
    }


    /**
     * Update device alarm information
     * @param deviceDTO
     * @return
     */
    public boolean updateDevicesAlarm(DeviceDTO deviceDTO){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceDTO.getDeviceId())
                .doc(   "alarm",deviceDTO.getAlarm(),
                        "level",deviceDTO.getLevel(),
                        "alarmName",deviceDTO.getAlarmName() );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error updating device alarm information");
            return false;
        }
    }

    /**
     * Update online status
     * @param deviceId
     * @param online
     * @return
     */
    public boolean updateOnline(String deviceId,Boolean online){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "online",online );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error updating online status");
            return false;
        }
    }


    public Pager<DeviceDTO> searchDevice(Long page,Long pageSize,String deviceId,String tags,Integer state){

        SearchRequest searchRequest=new SearchRequest("devices");

        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();

        //deviceId
        if( !Strings.isNullOrEmpty(deviceId) ){
            boolQueryBuilder.must(  QueryBuilders.wildcardQuery("deviceId", deviceId+"*" )  );
        }

        //tags
        if( !Strings.isNullOrEmpty(tags) ){
            boolQueryBuilder.must(  QueryBuilders.wildcardQuery("tag", "*"+tags+"*" )  );
        }

        //status
        if( state!=null ){
            boolQueryBuilder.must( QueryBuilders.termQuery("status",true) );
            //online
            if(state.intValue()==0){
                boolQueryBuilder.must( QueryBuilders.termQuery("online",true) );
            }
            //offline
            if(state.intValue()==1){
                boolQueryBuilder.must( QueryBuilders.termQuery("online",false) );
            }
            //general alarm
            if(state.intValue()==2){
                boolQueryBuilder.must( QueryBuilders.termQuery("level",1) );
            }
            //severe alarm
            if(state.intValue()==3){
                boolQueryBuilder.must( QueryBuilders.termQuery("level",2) );
            }
        }

        //page
        sourceBuilder.from(  (page.intValue()-1) * pageSize.intValue()  );
        sourceBuilder.size( pageSize.intValue() );
        sourceBuilder.trackTotalHits(true);//Paging query, you can get the full number of results

        sourceBuilder.sort("level", SortOrder.DESC);//Severe alarms are at the top of the list

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);


        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits searchHits = searchResponse.getHits();

            List<DeviceDTO> devices= Lists.newArrayList();
            for( SearchHit hit:searchHits ){
                String hitResult = hit.getSourceAsString();
                DeviceDTO deviceDTO=JsonUtil.getByJson(hitResult,DeviceDTO.class);
                devices.add(deviceDTO);
            }

            Pager<DeviceDTO> pager=new Pager<>( searchResponse.getHits().getTotalHits().value,pageSize );
            pager.setItems( devices );

            return pager;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Query device exceptions");
            return null;
        }


    }


    /**
     * Count all devices
     * @return
     */
    public Long getAllDeviceCount(){

        CountRequest countRequest=new CountRequest("devices");
        countRequest.query( QueryBuilders.matchAllQuery() );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * Count all offline devices
     * @return
     */
    public Long getOfflineCount(){

        CountRequest countRequest=new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must( QueryBuilders.termQuery("online",false)  );

        countRequest.query( boolQueryBuilder );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * Count the number of all alarmed devices
     * @return
     */
    public Long getAlarmCount(){

        CountRequest countRequest=new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must( QueryBuilders.termQuery("online",true)  );
        boolQueryBuilder.must( QueryBuilders.termQuery("alarm",true)  );
        countRequest.query( boolQueryBuilder );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


}
