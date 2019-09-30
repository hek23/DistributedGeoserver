FROM python:alpine
WORKDIR /usr/src/app
RUN apk add postgresql-dev
#RUN apk add libgeos-dev
#Patch
COPY patch.txt ./
RUN cat patch.txt >> /etc/apk/repositories
RUN cat /etc/apk/repositories
RUN apk add --update \
gcc \
libc-dev \ 
geos 
#Patch
#COPY patch.txt ./
#RUN awk FNR-1 /sbin/ldconfig > asd
#RUN cat patch.txt asd > /sbin/ldconfig
#RUN cat /sbin/ldconfig  
COPY requirements.txt ./
COPY *.py ./
RUN pip install --no-cache-dir -r requirements.txt

CMD [ "python", "./start" ]