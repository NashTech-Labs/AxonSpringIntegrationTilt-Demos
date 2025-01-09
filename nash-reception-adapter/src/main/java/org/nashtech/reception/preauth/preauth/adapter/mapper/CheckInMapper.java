package org.nashtech.reception.preauth.preauth.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage;
import org.nashtech.reception.preauth.preauth.adapter.service.MockCreditCheckService;
import org.nashtech.reception.preauth.preauth.commands.CheckInCommand;
import org.springframework.core.convert.converter.Converter;

/**
 * Mapper interface for converting {@link CheckInMessage} into {@link CheckInCommand}.
 *
 * <p>This interface extends Spring Integration's {@link Converter} and uses MapStruct
 * for mapping fields between the source and target objects. It includes custom mapping logic
 * to determine if the held amount can be authorized.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Maps nested fields from the {@code data} property in {@link CheckInMessage}.</li>
 *     <li>Custom expression for evaluating if the amount can be held.</li>
 * </ul>
 *
 * <p>Used in Spring Integration flows to transform incoming messages into Axon commands.</p>
 *
 * @see CheckInMessage
 * @see CheckInCommand
 * @see Converter
 * @see MockCreditCheckService
 *
 * */
@Mapper
public interface CheckInMapper extends Converter<CheckInMessage, CheckInCommand> {

    @Mapping(source = "data.reservationId", target = "reservationId")
    @Mapping(source = "data.checkInDate", target = "checkInDate")
    @Mapping(source = "data.roomNumber", target = "roomNumber")
    @Mapping(source = "data.roomType", target = "roomType")
    @Mapping(source = "data.hotelId", target = "hotelId")
    @Mapping(source = "data.amountHold", target = "amountHold")
    @Mapping(source = "data.currency", target = "currency")
    @Mapping(expression = "java(canAmountBeHold(source))" , target = "canAmountBeHold")
    @Override
    CheckInCommand convert(CheckInMessage source);

    default boolean canAmountBeHold(CheckInMessage source) {

        var mockCreditCheckService = new MockCreditCheckService();
         return source.getData().getAmountHold() >
                 mockCreditCheckService.checkCredit(source).getAvailableAmount();
    }
}
