package com.yyggee.eggs.controller.ds2;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.constants.ErrorCodes;
import com.yyggee.eggs.dto.common.BaseApiResponse;
import com.yyggee.eggs.dto.CookRequest;
import com.yyggee.eggs.exceptions.KitchenApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping(Endpoints.BASE_URL+"/admin")
public class EggsController {

    /**
     * Returns a 200 response at the '/wallet/add' path.
     * Check if service is running
     *
     * @return response entity
     */
    @PostMapping(value = Endpoints.EGG_ADD_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<BaseApiResponse> postBoilEggs(@PathVariable("id") String id, @RequestBody @Valid CookRequest payload) {
        try {
            log.debug("data from payload {}", payload.getUsername());
            return new ResponseEntity<>(
                    new BaseApiResponse("OK", "Not Implemented",
                            payload
                    ), HttpStatus.OK
            );
        } catch (KitchenApiException ex) {
            log.error("Egg error for create endpoint", ex);
            throw new KitchenApiException(ex.getErrorCode());
        } catch (Exception ex) {
            log.error("Undefined error for created endpoint", ex);
            throw new KitchenApiException(ErrorCodes.NOT_IMPLEMENTED_YET);
        }
    }
}
