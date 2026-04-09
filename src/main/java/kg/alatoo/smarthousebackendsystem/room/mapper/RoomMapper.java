package kg.alatoo.smarthousebackendsystem.room.mapper;

import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.payload.response.RoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(source = "home.id", target = "homeId")
    RoomResponse toResponse(Room room);

}