package org.nashtech.reception.preauth.preauth.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckOutMessage;
import org.nashtech.reception.preauth.preauth.commands.CheckOutCommand;
import org.springframework.core.convert.converter.Converter;

/**
 * Mapper interface for converting {@link CheckOutMessage} into {@link CheckOutCommand}.
 *
 * <p>This interface extends Spring Integration's {@link Converter} and uses MapStruct
 * for mapping fields between the source and target objects. It handles the transformation
 * of checkout-related messages into Axon commands.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Maps nested fields from the {@code data} property in {@link CheckOutMessage}.</li>
 *     <li>Simplifies integration by automating the conversion process.</li>
 * </ul>
 *
 * <p>Used in Spring Integration flows to transform checkout-related messages into Axon commands.</p>
 *
 * @see CheckOutMessage
 * @see CheckOutCommand
 * @see Converter
 */
@Mapper
public interface CheckOutMapper extends Converter<CheckOutMessage, CheckOutCommand> {

    @Mapping(source = "data.reservationId", target = "reservationId")
    @Mapping(source = "data.checkOutDate", target = "checkOutDate")
    @Mapping(source = "data.roomNumber", target = "roomNumber")
    @Mapping(source = "data.hotelId", target = "hotelId")
    @Mapping(source = "data.finalBillAmount", target = "finalBillAmount")
    @Mapping(source = "data.additionalCharges", target = "additionalCharges")
    @Override
    CheckOutCommand convert(CheckOutMessage source);
}
