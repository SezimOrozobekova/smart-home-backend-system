package kg.alatoo.smarthousebackendsystem.home.mapper;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.payload.response.HomeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HomeMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    HomeResponse toResponse(Home home);

}