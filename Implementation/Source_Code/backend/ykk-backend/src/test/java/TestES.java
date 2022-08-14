import com.fasterxml.jackson.core.JsonProcessingException;
import com.yikekong.YkkApplication;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.es.ESRepository;
import com.yikekong.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = YkkApplication.class)
@RunWith(SpringRunner.class)
public class TestES {

    @Autowired
    private ESRepository esRepository;


    @Test
    public void testAdd(){
        DeviceDTO deviceDTO=new DeviceDTO();
        deviceDTO.setDeviceId("000004");
        deviceDTO.setAlarm(true);
        deviceDTO.setAlarmName("Temperature Alarm");
        deviceDTO.setLevel(1);
        deviceDTO.setOnline(true);
        deviceDTO.setTag("Mall");
        deviceDTO.setStatus(true);
        esRepository.addDevices(deviceDTO);
    }


    @Test
    public void testSearchById(){

        DeviceDTO deviceDTO = esRepository.searchDeviceById("123456");
        try {
            String json = JsonUtil.serialize(deviceDTO);
            System.out.println(json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testAlarm(){
        DeviceDTO deviceDTO=new DeviceDTO();
        deviceDTO.setDeviceId("123456");
        deviceDTO.setAlarm(true);
        deviceDTO.setLevel(1);
        deviceDTO.setAlarmName("High temperature");

        esRepository.updateDevicesAlarm(deviceDTO);

    }


    @Test
    public void testOnline(){

        esRepository.updateOnline("123456",false);

    }


    @Test
    public void testCount(){

        Long allDeviceCount = esRepository.getAllDeviceCount();
        System.out.println("Total number of equipment："+allDeviceCount);

        Long offlineCount = esRepository.getOfflineCount();
        System.out.println("Off-line devices："+offlineCount);

        Long alarmCount = esRepository.getAlarmCount();
        System.out.println("Alarm devices："+alarmCount);

    }

}
