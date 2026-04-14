package kg.alatoo.smarthousebackendsystem.home.mapper;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.payload.request.CreateHomeRequest;
import kg.alatoo.smarthousebackendsystem.home.payload.response.HomeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HomeMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    HomeResponse toResponse(Home home);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Home toEntity(CreateHomeRequest request);
}