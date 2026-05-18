package com.banking.notification.infrastructure.notification.template;

import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateBuilder {

    public String buildSubject(Notification notification) {
        if (notification.getType() == NotificationType.TRANSFER_COMPLETED) {
            return "Transferencia completada";
        }

        if (notification.getType() == NotificationType.TRANSFER_FAILED) {
            return "Transferencia fallida";
        }

        return "Notificación bancaria";
    }

    public String buildBody(Notification notification) {
        if (notification.getType() == NotificationType.TRANSFER_COMPLETED) {
            return """
                    Hola,

                    Tu transferencia fue completada correctamente.

                    Código de transferencia: %s

                    Gracias por usar Banking System.
                    """.formatted(notification.getTransferId());
        }

        if (notification.getType() == NotificationType.TRANSFER_FAILED) {
            return """
                    Hola,

                    Tu transferencia no pudo ser completada.

                    Código de transferencia: %s

                    Por favor revisa el estado de tu operación.

                    Gracias por usar Banking System.
                    """.formatted(notification.getTransferId());
        }

        return """
                Hola,

                Tienes una nueva notificación bancaria.

                Código de transferencia: %s
                """.formatted(notification.getTransferId());
    }
}