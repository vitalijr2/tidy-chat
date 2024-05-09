#/bin/bash

export AWS_ACCESS_KEY=AKIAR4O6F3IRG355QXZ6
export AWS_REGION=us-east-2
export AWS_SECRET_ACCESS_KEY=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


./mvnw clean package aws:deployLambda -DskipTests=true -Paws-lambda

